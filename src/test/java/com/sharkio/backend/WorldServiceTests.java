package com.sharkio.backend;

import com.sharkio.backend.enums.WorldState;
import com.sharkio.backend.model.Player;
import com.sharkio.backend.model.World;
import com.sharkio.backend.repository.WorldRepository;
import com.sharkio.backend.service.FoodService;
import com.sharkio.backend.service.MineService;
import com.sharkio.backend.service.PlayerService;
import com.sharkio.backend.service.WorldService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

import static junit.framework.TestCase.*;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class WorldServiceTests {
    @Mock
    private WorldRepository worldRepositoryMock;

    @Mock
    private PlayerService playerServiceMock;

    @Mock
    private MineService mineService;

    @Mock
    private FoodService foodService;

    @InjectMocks
    private WorldService worldService;

    @Test
    public void testGetWorldWhenWorldExists() {
        // Arrange
        World existingWorld = new World();
        existingWorld.setX_dim(600);
        existingWorld.setY_dim(800);

        when(worldRepositoryMock.findAll()).thenReturn(Collections.singletonList(existingWorld));

        // Act
        World resultWorld = worldService.getWorld();

        // Assert
        assertNotNull(resultWorld);
        assertEquals(existingWorld, resultWorld);
    }

    @Test
    public void testGetWorldWhenNoWorldExists() {
        // Arrange
        when(worldRepositoryMock.findAll()).thenReturn(Collections.emptyList());

        // Act
        World resultWorld = worldService.getWorld();

        // Assert
        assertNotNull(resultWorld);
        assertEquals((float) 600, resultWorld.getX_dim());
        assertEquals((float) 600, resultWorld.getY_dim());
        assertTrue(resultWorld.getPlayers().isEmpty());
    }

    @Test
    public void testWorldInitWithFood() {
        // Arrange
        when(worldRepositoryMock.findAll()).thenReturn(Collections.emptyList());

        // Act
        World resultWorld = worldService.getWorld();

        // Assert
        assertEquals(resultWorld.getFoods().size(), 10);
    }

    @Test
    public void testJoin() {
        // Arrange
        World world = new World();
        world.setX_dim(600);
        world.setY_dim(800);

        Player player = new Player();
        player.setId(1);
        player.setPos_x(300);
        player.setPos_y(400);

        World savedWorld = new World();
        savedWorld.setX_dim(600);
        savedWorld.setY_dim(800);
        Set<Player> players = new HashSet<>();
        players.add(player);
        savedWorld.setPlayers(players);

        when(worldService.getWorld()).thenReturn(world);
        when(worldRepositoryMock.save(world)).thenReturn(savedWorld);
        when(playerServiceMock.addPlayer(any(Player.class))).thenReturn(player);

        // Act
        Player savedPlayer = worldService.join("");
        Set<Player> savedPlayers = savedWorld.getPlayers();

        // Assert
        assertNotNull(savedPlayer);
        assertEquals(player, savedPlayer);

        assertNotNull(savedPlayers);
        assertEquals(1, savedPlayers.size());
        assertTrue(savedPlayers.contains(player));
    }
}
