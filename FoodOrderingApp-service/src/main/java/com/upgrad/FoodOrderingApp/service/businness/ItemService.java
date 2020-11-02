package com.upgrad.FoodOrderingApp.service.businness;
import com.upgrad.FoodOrderingApp.service.common.UtilityProvider;
import com.upgrad.FoodOrderingApp.service.dao.*;
import com.upgrad.FoodOrderingApp.service.entity.*;
import com.upgrad.FoodOrderingApp.service.exception.ItemNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class ItemService {

    @Autowired
    ItemDao itemDao;

    @Autowired
    CategoryDao categoryDao;

    @Autowired
    RestaurantDao restaurantDao;

    @Autowired
    RestaurantItemDao restaurantItemDao;

    @Autowired
    OrderItemDao orderItemDao;

    @Autowired
    CategoryItemDao categoryItemDao;

    @Autowired
    OrderDao orderDao;

    @Autowired
    UtilityProvider utilityProvider;

    public List<ItemEntity> getItemsByPopularity(RestaurantEntity restaurantEntity) {
        List <OrdersEntity> ordersEntities = orderDao.getOrdersByRestaurant(restaurantEntity);
        List <ItemEntity> itemEntities = new LinkedList<>();
        ordersEntities.forEach(ordersEntity -> {
            List <OrderItemEntity> orderItemEntities = orderItemDao.getItemsByOrders(ordersEntity);
            orderItemEntities.forEach(orderItemEntity -> { //Looping in to get each tem from the OrderItemEntity.
                itemEntities.add(orderItemEntity.getItem());
            });
        });
        Map<String,Integer> itemCountMap = new HashMap<String,Integer>();
        itemEntities.forEach(itemEntity -> { //Looping in to count the frequency of Item ordered correspondingly updating the count.
            Integer count = itemCountMap.get(itemEntity.getUuid());
            itemCountMap.put(itemEntity.getUuid(),(count == null) ? 1 : count+1);
        });
        Map<String,Integer> sortedItemCountMap = utilityProvider.sortMapByValues(itemCountMap);
        List<ItemEntity> sortedItemEntites = new LinkedList<>();
        Integer count = 0;
        for(Map.Entry<String,Integer> item:sortedItemCountMap.entrySet()){
            if(count < 5) {
                sortedItemEntites.add(itemDao.getItemByUUID(item.getKey()));
                count = count+1;
            }else{
                break;
            }
        }
        return sortedItemEntites;
    }

    public ItemEntity getItemByUUID(String itemUuid) throws ItemNotFoundException {
        ItemEntity itemEntity = itemDao.getItemByUUID(itemUuid);
        if(itemEntity == null){
            throw new ItemNotFoundException("INF-003","No item by this id exist");
        }
        return itemEntity;
    }

    public List<ItemEntity> getItemsByCategoryAndRestaurant(String restaurantUuid, String categoryUuid) {
        RestaurantEntity restaurantEntity = restaurantDao.getRestaurantByUuid(restaurantUuid);
        CategoryEntity categoryEntity = categoryDao.getCategoryByUuid(categoryUuid);
        List<RestaurantItemEntity> restaurantItemEntities = restaurantItemDao.getItemsByRestaurant(restaurantEntity);
        List<CategoryItemEntity> categoryItemEntities = categoryItemDao.getItemsByCategory(categoryEntity);
        List<ItemEntity> itemEntities = new LinkedList<>();
        restaurantItemEntities.forEach(restaurantItemEntity -> {
            categoryItemEntities.forEach(categoryItemEntity -> {
                if(restaurantItemEntity.getItem().equals(categoryItemEntity.getItem())){
                    itemEntities.add(restaurantItemEntity.getItem());
                }
            });
        });
        return itemEntities;
    }
}
