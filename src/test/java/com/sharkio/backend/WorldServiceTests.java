package com.sharkio.backend;

import com.sharkio.backend.model.Player;
import com.sharkio.backend.model.World;
import com.sharkio.backend.repository.WorldRepository;
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

    @InjectMocks
    private WorldService worldService;

    @Test
    public void testGetWorldWhenWorldExists() {
        World existingWorld = new World();
        existingWorld.setX_dim(600);
        existingWorld.setY_dim(800);

        when(worldRepositoryMock.findAll()).thenReturn(Collections.singletonList(existingWorld));

        World resultWorld = worldService.getWorld();

        assertNotNull(resultWorld);
        assertEquals(existingWorld, resultWorld);
    }

    @Test
    public void testGetWorldWhenNoWorldExists() {
        when(worldRepositoryMock.findAll()).thenReturn(Collections.emptyList());

        World resultWorld = worldService.getWorld();

        assertNotNull(resultWorld);
        assertEquals(600, resultWorld.getX_dim());
        assertEquals(800, resultWorld.getY_dim());
        assertTrue(resultWorld.getPlayers().isEmpty());
    }

    @Test
    public void testJoin() {
        World world = new World();
        world.setX_dim(600);
        world.setY_dim(800);

        Player player = new Player();
        player.setId(1L);
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

        Player savedPlayer = worldService.join();

        assertNotNull(savedPlayer);
        assertEquals(player, savedPlayer);

        Set<Player> savedPlayers = savedWorld.getPlayers();
        assertNotNull(savedPlayers);
        assertEquals(1, savedPlayers.size());
        assertTrue(savedPlayers.contains(player));
    }

    /*@Test
    public void testJoinWhenPlayerNotAddedSuccessfully() {
        World existingWorld = new World();
        existingWorld.setX_dim(600);
        existingWorld.setY_dim(800);

        // Définir le comportement du mock de WorldRepository pour retourner le monde existant
        when(worldRepositoryMock.findAll()).thenReturn(Collections.singletonList(existingWorld));

        // Définir le comportement du mock de PlayerService pour retourner null, simulant un échec lors de l'ajout du joueur
        when(playerServiceMock.addPlayer(any(Player.class))).thenReturn(null);

        // Appeler la méthode à tester
        Player savedPlayer = worldService.join();

        // Vérifier le résultat
        assertNull(savedPlayer);

        // Vérifier que le monde n'a pas été modifié
        verify(worldRepositoryMock, never()).save(any(World.class));
    }*/
}
