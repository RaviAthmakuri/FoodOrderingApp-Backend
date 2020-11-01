//package com.upgrad.FoodOrderingApp.api.controller;
//
//
//import com.upgrad.FoodOrderingApp.api.model.CategoriesListResponse;
//import com.upgrad.FoodOrderingApp.api.model.CategoryDetailsResponse;
//import com.upgrad.FoodOrderingApp.api.model.CategoryListResponse;
//import com.upgrad.FoodOrderingApp.api.model.ItemList;
//import com.upgrad.FoodOrderingApp.service.businness.CategoryService;
//import com.upgrad.FoodOrderingApp.service.businness.ItemService;
//import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
//import com.upgrad.FoodOrderingApp.service.entity.ItemEntity;
//import com.upgrad.FoodOrderingApp.service.exception.CategoryNotFoundException;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.ArrayList;
//import java.util.LinkedList;
//import java.util.List;
//import java.util.UUID;
//
//// Category Controller Handles all  the Category related endpoints
//
//@RestController
//@RequestMapping("/")
//public class CategoryController {
//
//    @Autowired
//    CategoryService categoryService;
//
//    @CrossOrigin
//    @RequestMapping(method = RequestMethod.GET,path = "/category",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
//    public ResponseEntity<CategoriesListResponse> getAllCategories(){
//        List<CategoryEntity> categoryEntityList = categoryService.getAllCategoriesByName();
//            List<CategoryListResponse> categoryResponseList = new LinkedList<>();
//            categoryEntityList.forEach(categoryEntity -> {
//                CategoryListResponse categoryListResponse = new CategoryListResponse()
//                        .id(UUID.fromString(categoryEntity.getUuid()))
//                        .categoryName(categoryEntity.getCategoryName());
//                categoryResponseList.add(categoryListResponse);
//            });
//            CategoriesListResponse categoriesListResponse = new CategoriesListResponse().categories(categoryResponseList);
//            return new ResponseEntity<CategoriesListResponse>(categoriesListResponse, HttpStatus.OK);
//
//    }
//
//    @CrossOrigin
//    @RequestMapping(method = RequestMethod.GET,path = "/category/{category_id}",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
//    public ResponseEntity<CategoryDetailsResponse> getCategoryById(@PathVariable(value = "category_id")final String categoryUuid) throws CategoryNotFoundException {
//        CategoryEntity categoryEntity = categoryService.getCategoryById(categoryUuid);
//        List<ItemEntity> itemEntities = categoryEntity.getItems();
//        List<ItemList> itemResponseList = new LinkedList<>();
//        itemEntities.forEach(itemEntity -> {
//            ItemList itemList = new ItemList()
//                    .id(UUID.fromString(itemEntity.getUuid()))
//                    .price(itemEntity.getPrice())
//                    .itemName(itemEntity.getitemName())
//                    .itemType(ItemList.ItemTypeEnum.fromValue(itemEntity.getType().getValue()));
//            itemResponseList.add(itemList);
//        });
//        CategoryDetailsResponse categoryDetailsResponse = new CategoryDetailsResponse()
//                .categoryName(categoryEntity.getCategoryName())
//                .id(UUID.fromString(categoryEntity.getUuid()))
//                .itemList(itemResponseList);
//        return new ResponseEntity<CategoryDetailsResponse>(categoryDetailsResponse,HttpStatus.OK);
//    }
//
//}
