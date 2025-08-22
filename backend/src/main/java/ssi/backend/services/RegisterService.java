package ssi.backend.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Validator;
import ssi.backend.dtos.RegisterReposneDTO;
import ssi.backend.dtos.RegisterRequestDTO;
import ssi.backend.exceptions.BadRequestException;
import ssi.backend.repositories.BuyerRepository;
import ssi.backend.repositories.SellerRepository;
import ssi.backend.utils.ListMapper;
import ssi.backend.entities.Buyer;
import ssi.backend.entities.Seller;

@Service
public class RegisterService {
    @Autowired
    private BuyerRepository buyerRepository;
    @Autowired
    private SellerRepository sellerRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private ListMapper listMapper;
    @Autowired
    private Validator validator;

    public RegisterReposneDTO registerBuyer(RegisterRequestDTO buyer) {

    }
}
