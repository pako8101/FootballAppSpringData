package softuni.exam.service;


import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import softuni.exam.domain.dtos.TeamSeedRootDto;
import softuni.exam.domain.entities.Picture;
import softuni.exam.domain.entities.Team;
import softuni.exam.repository.TeamRepository;
import softuni.exam.util.ValidatorUtil;
import softuni.exam.util.XmlParser;


import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;


@Service

public class TeamServiceImpl implements TeamService {

    private final TeamRepository teamRepository;
    private final ModelMapper modelMapper;
    private final ValidatorUtil validatorUtil;
    private final XmlParser xmlParser;
    private final PictureService pictureService;
    private final static String PATH = "src/main/resources/files/xml/teams.xml";

    @Autowired
    public TeamServiceImpl(TeamRepository teamRepository, ModelMapper modelMapper, @Qualifier("validationUtil") ValidatorUtil validatorUtil, XmlParser xmlParser, PictureService pictureService) {
        this.teamRepository = teamRepository;
        this.modelMapper = modelMapper;
        this.validatorUtil = validatorUtil;
        this.xmlParser = xmlParser;
        this.pictureService = pictureService;
    }


    @Override

    public String importTeams() throws JAXBException, FileNotFoundException {
        StringBuilder sb = new StringBuilder();
        JAXBContext jaxbContext = JAXBContext.newInstance(TeamSeedRootDto.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        TeamSeedRootDto teamSeedRootDto =
                (TeamSeedRootDto) unmarshaller.unmarshal(new File(PATH));

        teamSeedRootDto.getTeams()
                .forEach(teamSeedDto -> {
                    boolean isValid = this.validatorUtil.isValid(teamSeedDto);
                    if (isValid) {

                        if (this.teamRepository.findTeamByName(teamSeedDto.getName()) == null) {
                            Team team = this.modelMapper.map(teamSeedDto, Team.class);
                            Picture picture = this.pictureService.getPictureUrl(
                                    teamSeedDto.getPicture().getUrl());
                            if (picture == null) {
                                sb.append("Invalid team");
                            }
                            team.setPicture(picture);

                            this.teamRepository.saveAndFlush(team);
                            sb.append(String.format("Successfully imported picture - %s", teamSeedDto.getName()));
                        } else {
                            sb.append("Already in DB");
                        }

                    } else {
                        sb.append("Invalid team");
                    }
                    sb.append(System.lineSeparator());
                });


        return sb.toString().trim();
    }

    @Override
    public boolean areImported() {
        return this.teamRepository.count() > 0;
    }

    @Override
    public String readTeamsXmlFile() throws IOException {
        return Files.readString(Path.of(PATH));
    }

    @Override
    public Team getTeamByName(String name) {
        return this.teamRepository.findTeamByName(name);
    }

}
