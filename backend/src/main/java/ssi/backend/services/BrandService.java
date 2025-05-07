package ssi.backend.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ssi.backend.dtos.BrandDTO;
import ssi.backend.repositories.BrandRepository;
import ssi.backend.utils.ListMapper;

import java.util.List;

@Service
public class BrandService {
    @Autowired
    private BrandRepository brandRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private ListMapper listMapper;

    public List<BrandDTO> getAllBrands() {
        return listMapper.mapList(brandRepository.findAll(), BrandDTO.class, modelMapper);
    }
}
