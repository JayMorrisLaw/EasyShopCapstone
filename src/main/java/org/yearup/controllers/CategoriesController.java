package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.data.CategoryDao;
import org.yearup.data.ProductDao;
import org.yearup.models.Category;
import org.yearup.models.Product;

import java.util.List;

// add the annotations to make this a REST controller
@RestController
// add the annotation to make this controller the endpoint for the following url
    // http://localhost:8080/categories
@RequestMapping("/categories") // setting URL for all methods
// add annotation to allow cross site origin requests
@CrossOrigin // allows websites to interact with API
public class CategoriesController
{
    private CategoryDao categoryDao;
    private ProductDao productDao;

    // create an Autowired controller to inject the categoryDao and ProductDao
    @Autowired
    public CategoriesController(CategoryDao categoryDao, ProductDao productDao){
        this.categoryDao = categoryDao;
        this.productDao = productDao;
    }
    // add the appropriate annotation for a get action
    @GetMapping // method to get requests to categories
    public List<Category> getAll()
    {
        // find and return all categories
        return categoryDao.getAllCategories(); // gets categories from database
    }

    // add the appropriate annotation for a get action
    @GetMapping ("{id}") // gets requests and grabs category by id entered
    public Category getById(@PathVariable int id)
    {
        // get the category by id
       Category category = categoryDao.getById(id);

        if (category == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found");
        } // looks for category entered and if not found it throws a 404 error

        return category;
    }

    // the url to return all products in category 1 would look like this
    // https://localhost:8080/categories/1/products
    @GetMapping("{categoryId}/products") // gets products that belong to a specific category
    public List<Product> getProductsById(@PathVariable int categoryId)
    {
        // get a list of product by categoryId
        return productDao.getByCategoryId(categoryId);// calls this method from product DAO to grab products
    }

    // add annotation to call this method for a POST action
    // add annotation to ensure that only an ADMIN can call this function
    @PostMapping // post to create a new category
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus (HttpStatus.CREATED)// return 201 if successful
    public Category addCategory(@RequestBody Category category)
    {
        // insert the category
        return categoryDao.create(category);// adds category into database and returns it
    }

    // add annotation to call this method for a PUT (update) action - the url path must include the categoryId
    // add annotation to ensure that only an ADMIN can call this function
    @PutMapping("{id}")
    @PreAuthorize("hasRole('ADMIN')") // only admins can update
    public void updateCategory(@PathVariable int id, @RequestBody Category category)
    {
        // update the category by id
        categoryDao.update(id,category);
    }


    // add annotation to call this method for a DELETE action - the url path must include the categoryId
    // add annotation to ensure that only an ADMIN can call this function
    @DeleteMapping ("{id}")
    @PreAuthorize("hasRole('ADMIN')") // only admins can delete
    @ResponseStatus(HttpStatus.NO_CONTENT) // if the request was successful not content is returned because it was deleted
    public void deleteCategory(@PathVariable int id)
    {
        // delete the category by id
        categoryDao.delete(id); // tells DAO to remove the category 
    }
}
