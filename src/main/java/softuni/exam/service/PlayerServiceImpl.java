package softuni.exam.service;


import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import softuni.exam.domain.dtos.PlayerSeedDto;
import softuni.exam.domain.entities.Picture;
import softuni.exam.domain.entities.Player;
import softuni.exam.domain.entities.Team;
import softuni.exam.repository.PlayerRepository;
import softuni.exam.util.ValidatorUtil;


import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;


@Service

public class PlayerServiceImpl implements PlayerService {
    private final ModelMapper modelMapper;
    private final ValidatorUtil validatorUtil;
    private final Gson gson;
    private final PictureService pictureService;
    private final TeamService teamService;
    private final PlayerRepository playerRepository;
    private final static String PATH = "src/main/resources/files/json/players.json";

    @Autowired
    public PlayerServiceImpl(ModelMapper modelMapper, @Qualifier("validationUtil") ValidatorUtil validatorUtil, Gson gson, PictureService pictureService, TeamService teamService, PlayerRepository playerRepository) {
        this.modelMapper = modelMapper;
        this.validatorUtil = validatorUtil;
        this.gson = gson;
        this.pictureService = pictureService;
        this.teamService = teamService;
        this.playerRepository = playerRepository;
    }

    @Override
    public String importPlayers() throws IOException {
        StringBuilder sb = new StringBuilder();

        Arrays.stream(gson.fromJson(readPlayersJsonFile(), PlayerSeedDto[].class))
                .forEach(playerSeedDto -> {
                    boolean isValid = validatorUtil.isValid(playerSeedDto);
                    if (isValid) {
                        if (this.playerRepository.findByFirstNameAndLastName(playerSeedDto.
                                getFirstName(), playerSeedDto.getLastName()) == null) {
                            Player player = modelMapper.map(playerSeedDto, Player.class);

                            Team team = this.teamService.getTeamByName(playerSeedDto.getTeam().getName());
                            Picture picture = this.pictureService.getPictureUrl(playerSeedDto.getPicture().getUrl());
                            player.setPicture(picture);
                            player.setTeam(team);
                            this.playerRepository.saveAndFlush(player);
                            sb.append(String.format("Successfully imported player: %s %s", player.getFirstName(), player.getLastName()));
                        } else {
                            sb.append("Already in DB");
                        }

                    } else {
                        sb.append("Invalid player");
                    }

                    sb.append(System.lineSeparator());
                });


        return sb.toString().trim();
    }

    @Override
    public boolean areImported() {
        return this.playerRepository.count() > 0;
    }

    @Override
    public String readPlayersJsonFile() throws IOException {
        return Files.readString(Path.of(PATH));
    }

    @Override
    public String exportPlayersWhereSalaryBiggerThan() {
        StringBuilder sb = new StringBuilder();
this.playerRepository
        .findAllBySalaryGreaterThanOrderBySalaryDesc(BigDecimal
                .valueOf(100000))
        .forEach(player -> {
            sb.append(String.format("Player name: %s %s \n" +
                    "\tNumber: %d\n" +
                    "\tSalary: %.2f\n" +
                    "\tTeam: %s\n",
                    player.getFirstName(),player.getLastName(),
                    player.getNumber(),
                    player.getSalary(),player.getTeam().getName()))
                    .append(System.lineSeparator());
        });

        return sb.toString().trim();
    }

    @Override
    public String exportPlayersInATeam() {
        StringBuilder sb = new StringBuilder();

         this.playerRepository.findAllByTeamName("North Hub")
                .forEach(player -> {
                    sb.append(String.format(" Player name: %s" +
                                    "%s - %s \n" +
                                    "Number %d\n", player.getFirstName(),player.getLastName()
                            ,player.getPosition(),player.getNumber()))
                            .append(System.lineSeparator());
                });

        return sb.toString().trim();
    }


}
