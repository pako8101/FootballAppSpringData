package softuni.exam.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import softuni.exam.domain.dtos.PictureSeedDto;
import softuni.exam.domain.dtos.PictureSeedRootDto;
import softuni.exam.domain.entities.Picture;
import softuni.exam.repository.PictureRepository;
import softuni.exam.util.ValidatorUtil;
import softuni.exam.util.XmlParser;


import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;


@Service
public class PictureServiceImpl implements PictureService {
private final PictureRepository pictureRepository;
private final ModelMapper modelMapper;
private final ValidatorUtil validatorUtil;
private final XmlParser xmlParser;
private final static String PATH = "src/main/resources/files/xml/pictures.xml";
@Autowired
    public PictureServiceImpl(PictureRepository pictureRepository,
                              ModelMapper modelMapper,
                              @Qualifier("validationUtil") ValidatorUtil validatorUtil,
                              XmlParser xmlParser) {
        this.pictureRepository = pictureRepository;
        this.modelMapper = modelMapper;
    this.validatorUtil = validatorUtil;


    this.xmlParser = xmlParser;
}

    @Override
    public String importPictures() throws IOException, JAXBException {
        StringBuilder sb = new StringBuilder();
        JAXBContext jaxbContext = JAXBContext.newInstance(PictureSeedRootDto.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        PictureSeedRootDto pictureSeedRootDto =
                (PictureSeedRootDto) unmarshaller.unmarshal(new File(PATH));
pictureSeedRootDto.getPictures().stream()
        .filter(pictureSeedDto -> {
            boolean isValid = this.validatorUtil.isValid(pictureSeedDto);
//if (this.pictureRepository.findByUrl(pictureSeedDto.getUrl())==null){
//    isValid= false;
//}
            sb.append(isValid ? String.format("Successfully imported picture - %s",
                    pictureSeedDto.getUrl()) : "Invalid picture")
                    .append(System.lineSeparator());
            return isValid;
        })
        .map(pictureSeedDto -> modelMapper.map(pictureSeedDto, Picture.class))
        .forEach(this.pictureRepository::saveAndFlush);


    return sb.toString().trim();
    }

    @Override
    public boolean areImported() {
        return this.pictureRepository.count()>0;
    }

    @Override
    public String readPicturesXmlFile() throws IOException {
        return Files.readString(Path.of(PATH)) ;

    }

    @Override
    public Picture getPictureUrl(String url) {
        return this.pictureRepository.findByUrl(url);
    }


}
