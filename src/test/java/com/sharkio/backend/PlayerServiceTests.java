package com.sharkio.backend;

import com.sharkio.backend.model.Player;
import com.sharkio.backend.model.World;
import com.sharkio.backend.repository.PlayerRepository;
import com.sharkio.backend.repository.WorldRepository;
import com.sharkio.backend.service.PlayerService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.lang.reflect.Array;
import java.util.*;

import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PlayerServiceTests {
    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private WorldRepository worldRepository;

    @InjectMocks
    private PlayerService playerService;

    @Test
    public void getPlayers_ShouldReturnAllPlayers() {
        // Arrange
        List<Player> expectedPlayers = new ArrayList<>();
        expectedPlayers.add(new Player());
        expectedPlayers.add(new Player());

        when(playerRepository.findAll()).thenReturn(expectedPlayers);

        // Act
        Iterator<Player> actualPlayers_it = playerService.getPlayers().iterator();
        List<Player> actualPlayers = new ArrayList<>();

        while (actualPlayers_it.hasNext()) {
            actualPlayers.add(actualPlayers_it.next());
        }

        // Assert
        Assert.assertEquals(expectedPlayers.size(), actualPlayers.size());
    }

    @Test
    public void getPlayers_ShouldReturnNothing() {
        // Arrange
        List<Player> expectedPlayers = new ArrayList<>();

        when(playerRepository.findAll()).thenReturn(expectedPlayers);

        // Act
        Iterator<Player> actualPlayers_it = playerService.getPlayers().iterator();
        List<Player> actualPlayers = new ArrayList<>();

        while (actualPlayers_it.hasNext()) {
            actualPlayers.add(actualPlayers_it.next());
        }

        // Assert
        Assert.assertEquals(expectedPlayers.size(), actualPlayers.size());
    }

    @Test
    public void getById_ShouldThrowExceptionWhenIdIsNull() {
        // Arrange
        Integer id = null;

        // Act and Assert
        assertThrows(RuntimeException.class, () -> playerService.getById(id));
    }

    @Test
    public void getById_ShouldThrowExceptionWhenPlayerNotFound() {
        // Arrange
        Integer id = 100;
        when(playerRepository.findById(id)).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(RuntimeException.class, () -> playerService.getById(id));
    }

    @Test
    public void getById_ShouldReturnPlayerWithCorrectProperties() {
        // Arrange
        Integer id = 1;
        Player expectedPlayer = new Player();
        expectedPlayer.setPos_x(5.0f);
        expectedPlayer.setPos_y(10.0f);
        when(playerRepository.findById(id)).thenReturn(Optional.of(expectedPlayer));

        // Act
        Player actualPlayer = playerService.getById(id);

        // Assert
        Assert.assertEquals(5.0f, actualPlayer.getPos_x(), 0.001f);
        Assert.assertEquals(10.0f, actualPlayer.getPos_y(), 0.001f);
    }

    @Test
    public void addPlayer_ShouldAddPlayerAndReturnIt() {
        // Arrange
        Player playerToAdd = new Player();
        playerToAdd.setPos_x(5.0f);
        playerToAdd.setPos_y(10.0f);
        when(playerRepository.save(playerToAdd)).thenReturn(playerToAdd);

        // Act
        Player addedPlayer = playerService.addPlayer(playerToAdd);

        // Assert
        Assert.assertEquals(playerToAdd, addedPlayer);
        verify(playerRepository).save(playerToAdd);
    }

    @Test
    public void addPlayer_ShouldThrowExceptionWhenPlayerIsNull() {
        // Arrange
        Player playerToAdd = null;

        // Assert
        assertThrows(RuntimeException.class, () -> playerService.addPlayer(playerToAdd));

    }

    @Test
    public void addPlayer_ShouldThrowExceptionWhenPlayerIdAlreadyExists() {
        // Arrange
        Player existingPlayer = new Player();
        existingPlayer.setId(1);
        List<Player> players = new ArrayList<>(List.of(existingPlayer));

        when(playerRepository.existsById(1)).thenReturn(true);
        when(playerService.getPlayers()).thenReturn(players);

        Player playerToAdd = new Player();
        playerToAdd.setId(1);

        // Act and Assert
        assertThrows(RuntimeException.class, () -> playerService.addPlayer(playerToAdd));
    }

    @Test
    public void move_ShouldMovePlayerAndReturnIt() {
        // Arrange
        Integer id = 1;
        float newX = 5.0f;
        float newY = 10.0f;
        Player player = new Player();
        World world = new World();
        world.setX_dim(100);
        world.setY_dim(200);
        when(playerRepository.findById(id)).thenReturn(Optional.of(player));
        when(worldRepository.findAll()).thenReturn(Collections.singletonList(world));

        // Act
        Player movedPlayer = playerService.move(id, newX, newY);

        // Assert
        Assert.assertEquals(newX, movedPlayer.getPos_x(), 0.001f);
        Assert.assertEquals(newY, movedPlayer.getPos_y(), 0.001f);
        verify(playerRepository).save(player);
    }

    @Test
    public void move_ShouldThrowExceptionWhenPlayerNotFound() {
        // Arrange
        Integer id = 2;
        when(playerRepository.findById(id)).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(RuntimeException.class, () -> playerService.move(id, 0, 0));
    }

    @Test
    public void move_ShouldThrowExceptionWhenNewCoordinatesAreOutOfBounds() {
        // Arrange
        World world = new World();
        world.setX_dim(300);
        world.setY_dim(300);

        Integer id = 1;
        float newX = 1500.0f;
        float newY = 350.0f;
        when(playerRepository.findById(id)).thenReturn(Optional.of(new Player()));
        when(worldRepository.findAll()).thenReturn(Collections.singletonList(world));

        // Act and Assert
        assertThrows(RuntimeException.class, () -> playerService.move(id, newX, newY));
    }

    @Test
    public void move_ShouldMovePlayerAtWorldLimitAndReturnIt() {
        // Arrange
        Integer id = 1;
        float newX = 300.0f;
        float newY = 0.0f;
        Player player = new Player();

        World world = new World();
        world.setX_dim(300);
        world.setY_dim(300);

        when(playerRepository.findById(id)).thenReturn(Optional.of(player));
        when(worldRepository.findAll()).thenReturn(Collections.singletonList(world));

        // Act
        Player movedPlayer = playerService.move(id, newX, newY);

        // Assert
        Assert.assertEquals(newX, movedPlayer.getPos_x(), 0.001f);
        Assert.assertEquals(newY, movedPlayer.getPos_y(), 0.001f);
        verify(playerRepository).save(player);
    }

    @Test
    public void testDeletePlayer_successful() {
        // Arrange
        Player player = new Player();
        player.setId(1);
        World world = new World();
        Set<Player> players = new HashSet<>();
        players.add(player);
        world.setPlayers(players);

        when(worldRepository.findAll()).thenReturn(new ArrayList<>(List.of(world)));
        when(playerRepository.findById(1)).thenReturn(Optional.of(player));

        // Act
        Player deletedPlayer = playerService.delete(player.getId());

        // Assert
        Assert.assertEquals(deletedPlayer, player);
        Assert.assertEquals(world.getPlayers(), new HashSet<Player>());
    }

    @Test
    public void testDeletePlayer_notFound() {
        Integer nonExistentId = 999;

        // Act and assert
        assertThrows(RuntimeException.class, () -> playerService.delete(nonExistentId));
    }



}
