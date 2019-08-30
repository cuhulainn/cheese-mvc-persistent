package org.launchcode.controllers;

import org.launchcode.models.Category;
import org.launchcode.models.Cheese;
import org.launchcode.models.data.CategoryDao;
import org.launchcode.models.data.CheeseDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;

/**
 * Created by LaunchCode
 */
@Controller
@RequestMapping("cheese")
public class CheeseController {

    @Autowired
    private CheeseDao cheeseDao;

    @Autowired
    private CategoryDao categoryDao;

    // Request path: /cheese
    @RequestMapping(value = "")
    public String index(Model model) {

        model.addAttribute("cheeses", cheeseDao.findAll());
        model.addAttribute("title", "My Cheesy Cheeses");

        return "cheese/index";
    }

    @RequestMapping(value = "add", method = RequestMethod.GET)
    public String displayAddCheeseForm(Model model) {
        model.addAttribute("title", "Add Cheese");
        model.addAttribute(new Cheese());
        model.addAttribute("categories", categoryDao.findAll());
        return "cheese/add";
    }

    @RequestMapping(value = "add", method = RequestMethod.POST)
    public String processAddCheeseForm(@ModelAttribute  @Valid Cheese newCheese,
                                       Errors errors, @RequestParam int categoryId, Model model) {

        if (errors.hasErrors()) {
            model.addAttribute("title", "Add Cheese");
            model.addAttribute("categories", categoryDao.findAll());
            return "cheese/add";
        }

        Category cat = categoryDao.findOne(categoryId);
        newCheese.setCategory(cat);
        cheeseDao.save(newCheese);
        return "redirect:";
    }

    @RequestMapping(value = "remove", method = RequestMethod.GET)
    public String displayRemoveCheeseForm(Model model) {
        model.addAttribute("cheeses", cheeseDao.findAll());
        model.addAttribute("title", "Remove Cheese");
        return "cheese/remove";
    }

    @RequestMapping(value = "remove", method = RequestMethod.POST)
    public String processRemoveCheeseForm(@RequestParam int[] cheeseIds) {

        for (int cheeseId : cheeseIds) {
            cheeseDao.delete(cheeseId);
        }

        return "redirect:";
    }

    @RequestMapping(value="edit/{cheeseId}", method = RequestMethod.GET)
    public String displayEditForm(Model model, @PathVariable int cheeseId) {
        model.addAttribute("cheese", cheeseDao.findOne(cheeseId));
        model.addAttribute("categories", categoryDao.findAll());
        model.addAttribute("title", "Edit Cheese");
        return "cheese/edit";
    }

    @RequestMapping(value="edit", method = RequestMethod.POST)
    public String processEditForm(@ModelAttribute @Valid Cheese editedCheese, @RequestParam int category, @RequestParam int id, Errors errors, Model model) {

        if (errors.hasErrors()) {
            model.addAttribute("cheese", cheeseDao.findOne(id));
            model.addAttribute("categories", categoryDao.findAll() );
            return "redirect: /cheese/edit/" + id;
        }

        Cheese cheeseToEdit = cheeseDao.findOne(id);
        cheeseToEdit.setName(editedCheese.getName());
        cheeseToEdit.setDescription(editedCheese.getDescription());
        cheeseToEdit.setCategory(categoryDao.findOne(category));
//        cheeseToEdit.setCheeseRating(editedCheese.getCheeseRating());
        cheeseDao.save(cheeseToEdit);
        return "redirect: ";
    }

    @RequestMapping(value="category/{categoryId}", method = RequestMethod.GET)
    public String category(@PathVariable int categoryId, Model model) {

        Category cat = categoryDao.findOne(categoryId);
        ArrayList<Cheese> cheesesInCat = new ArrayList<>();

        for (Cheese cheese : cheeseDao.findAll()) {
            if (cheese.getCategory() == cat) {
                cheesesInCat.add(cheese);
            }
        }
        model.addAttribute("title", "Cheeses in the '" + cat.getName() + "' Category");
        model.addAttribute("cheeses", cheesesInCat);
        return "cheese/index";
    }
}
