package com.sharkio.backend;

import com.sharkio.backend.model.Food;
import com.sharkio.backend.repository.FoodRepository;
import com.sharkio.backend.repository.WorldRepository;
import com.sharkio.backend.service.FoodService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FoodServiceTests {
    @Mock
    private FoodRepository foodRepository;

    @Mock
    private WorldRepository worldRepository;

    @InjectMocks
    private FoodService foodService;

    @Test
    public void getFood_ShouldReturnAllFoods() {
        // Arrange
        List<Food> exprectedFood = new ArrayList<>();
        exprectedFood.add(new Food());
        exprectedFood.add(new Food());

        when(foodRepository.findAll()).thenReturn(exprectedFood);

        // Act
        Iterator<Food> actualFood_it = foodService.getFoods().iterator();
        List<Food> actualFoods = new ArrayList<>();

        while (actualFood_it.hasNext()) {
            actualFoods.add(actualFood_it.next());
        }

        // Assert
        Assert.assertEquals(exprectedFood.size(), actualFoods.size());
    }

    @Test
    public void getFood_ShouldReturnNothing() {
        // Arrange
        List<Food> expectedFood = new ArrayList<>();

        when(foodRepository.findAll()).thenReturn(expectedFood);

        // Act
        Iterator<Food> actualFood_it = foodService.getFoods().iterator();
        List<Food> actualFoods = new ArrayList<>();

        while (actualFood_it.hasNext()) {
            actualFoods.add(actualFood_it.next());
        }

        // Assert
        Assert.assertEquals(expectedFood.size(), actualFoods.size());
    }

    @Test
    public void getById_ShouldThrowExceptionWhenIdIsNull() {
        // Arrange
        Integer id = null;

        // Act and Assert
        assertThrows(RuntimeException.class, () -> foodService.getById(id));
    }

    @Test
    public void getById_ShouldThrowExceptionWhenFoodNotFound() {
        // Arrange
        Integer id = 100;
        when(foodRepository.findById(id)).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(RuntimeException.class, () -> foodService.getById(id));
    }

    @Test
    public void getById_ShouldReturnFoodWithCorrectProperties() {
        // Arrange
        Integer id = 1;
        Food expectedFood = new Food();
        expectedFood.setPos_x(5.0f);
        expectedFood.setPos_y(10.0f);
        when(foodRepository.findById(id)).thenReturn(Optional.of(expectedFood));

        // Act
        Food actualFood = foodService.getById(id);

        // Assert
        Assert.assertEquals(5.0f, actualFood.getPos_x(), 0.001f);
        Assert.assertEquals(10.0f, actualFood.getPos_y(), 0.001f);
    }

    @Test
    public void addFood_ShouldAddFoodAndReturnIt() {
        // Arrange
        Food foodToAdd = new Food();
        foodToAdd.setPos_x(5.0f);
        foodToAdd.setPos_y(10.0f);
        when(foodRepository.save(foodToAdd)).thenReturn(foodToAdd);

        // Act
        Food addedFood = foodService.addFood(foodToAdd);

        // Assert
        Assert.assertEquals(foodToAdd, addedFood);
        verify(foodRepository).save(foodToAdd);
    }

    @Test
    public void addFood_ShouldThrowExceptionWhenFoodIsNull() {
        // Arrange
        Food foodToAdd = null;

        // Assert
        assertThrows(RuntimeException.class, () -> foodService.addFood(foodToAdd));

    }

    @Test
    public void addFood_ShouldThrowExceptionWhenFoodIdAlreadyExists() {
        // Arrange
        Food existingFood = new Food();
        existingFood.setId(1);
        List<Food> foods = new ArrayList<>(List.of(existingFood));

        when(foodRepository.existsById(1)).thenReturn(true);
        when(foodService.getFoods()).thenReturn(foods);

        Food foodToAdd = new Food();
        foodToAdd.setId(1);

        // Act and Assert
        assertThrows(RuntimeException.class, () -> foodService.addFood(foodToAdd));
    }
}
