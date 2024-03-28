package com.roadsignssimulator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

@Controller
public class RoadSignController {
    private final RoadSignRepository roadSignRepository;
    private final Random random;
    private final List<Integer> successIds;
    private int successCounter = 0;
    private int counter = 0;

    @Autowired
    public RoadSignController(RoadSignRepository roadSignRepository) {
        this.roadSignRepository = roadSignRepository;
        this.random = new Random(System.currentTimeMillis());
        this.successIds = new ArrayList<>();
    }

    @GetMapping("/")
    public String index() {
        resetCounters();
        return "index";
    }

    @GetMapping("/test")
    public String test(Model model, HttpServletResponse response, @CookieValue("success") String success) {
        int countOfRoadSigns = (int) roadSignRepository.count();
        int successId;

        do {
            successId = Math.abs(random.nextInt(countOfRoadSigns) + 1);
        } while (successIds.contains(successId));

        successIds.add(successId);
        setRoadSignsDetails(countOfRoadSigns, successId, model, success, response);

        return "test";
    }

    @GetMapping("/training")
    public String training(Model model, HttpServletResponse response, @CookieValue("success") String success) {
        int countOfRoadSigns = (int) roadSignRepository.count();
        int successId = Math.abs(random.nextInt(countOfRoadSigns) + 1);
        setRoadSignsDetails(countOfRoadSigns, successId, model, success, response);
        return "training";
    }

    @GetMapping("/show")
    public String show(Model model) {
        resetCounters();
        Iterable<RoadSign> roadSigns = roadSignRepository.findAll();
        model.addAttribute("roadSigns", roadSigns);
        return "road_signs";
    }

    @GetMapping("/image/show/{id}")
    @ResponseBody
    public void showImage(@PathVariable("id") Integer id, HttpServletResponse response) throws IOException {
        Optional<RoadSign> roadSign = roadSignRepository.findById(id);
        response.getOutputStream().write(roadSign.get().getImage());
        response.getOutputStream().close();
    }

    private void resetCounters() {
        successCounter = 0;
        counter = 0;
        successIds.clear();
    }

    private void setRoadSignsDetails(int countOfRoadSigns, int successId, Model model, String success,
                                     HttpServletResponse response) {
        List<Integer> indexes = getIndexes(countOfRoadSigns, successId);
        List<String> titles = getTitles(indexes);
        addToModel(model, titles, indexes, successId, success, response);
    }

    private void addToModel(Model model, List<String> titles, List<Integer> indexes, int successId, String success,
                            HttpServletResponse response) {
        model.addAttribute("titles", titles);
        model.addAttribute("indexes", indexes);
        model.addAttribute("successId", successId);
        model.addAttribute("counter", counter++);

        if (success.equals("1")) {
            Cookie cookie = new Cookie("successCounter", String.valueOf(++successCounter));
            response.addCookie(cookie);
        }

        model.addAttribute("successCounter", successCounter);
    }

    private List<Integer> getIndexes(int countOfRoadSigns, int successId) {
        List<Integer> indexes = getIndexesForButtons(countOfRoadSigns);

        if (!indexes.contains(successId)) {
            indexes.set(random.nextInt(indexes.size()), successId);
        }

        return indexes;
    }

    private List<Integer> getIndexesForButtons(int size) {
        List<Integer> arrayOfIndexes = new ArrayList<>(size);

        for (int i = 1; i <= size; i++) {
            arrayOfIndexes.add(i);
        }

        Collections.shuffle(arrayOfIndexes);
        arrayOfIndexes = arrayOfIndexes.subList(0, 4);

        return arrayOfIndexes;
    }

    private List<String> getTitles(List<Integer> indexes) {
        List<String> titles = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            titles.add(roadSignRepository.findById(indexes.get(i)).get().getTitle());
        }

        return titles;
    }
}