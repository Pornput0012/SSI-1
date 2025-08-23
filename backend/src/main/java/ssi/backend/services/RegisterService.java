package ssi.backend.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ssi.backend.dtos.BuyerRequestDTO;
import ssi.backend.dtos.BuyerResponseDTO;
import ssi.backend.dtos.SellerRequestDTO;
import ssi.backend.entities.Buyer;
import ssi.backend.entities.Seller;
import ssi.backend.repositories.BuyerRepository;
import ssi.backend.repositories.SellerRepository;
import ssi.backend.exceptions.BadRequestException;

import java.util.List;

@Service
public class RegisterService {
    @Autowired
    private BuyerRepository buyerRepository;
    @Autowired
    private SellerRepository sellerRepository;
    @Autowired
    private ModelMapper modelMapper;

    public List<Buyer> getAllBuyers() {
        return buyerRepository.findAll();
    }

    public List<Seller> getAllSellers() {
        return sellerRepository.findAll();
    }

    public List<Buyer> getAllEmailBuyers(String email) {
        return buyerRepository.findByEmail(email);
    }

    public List<Seller> getAllEmailSellers(String email) {
        return sellerRepository.findByEmail(email);
    }

    public Buyer createBuyer(BuyerRequestDTO buyer) {
        if (!getAllEmailBuyers(buyer.getEmail()).isEmpty() ||
            !getAllEmailSellers(buyer.getEmail()).isEmpty()) {
            throw new BadRequestException("Email already exists: " + buyer.getEmail());
        }

        Buyer creatingBuyer = modelMapper.map(buyer, Buyer.class);
        creatingBuyer.setId(null);
        return buyerRepository.save(creatingBuyer);
        
    }

    public Seller createSeller(SellerRequestDTO seller) {
        if (!getAllEmailBuyers(seller.getEmail()).isEmpty() ||
            !getAllEmailSellers(seller.getEmail()).isEmpty()) {
            throw new BadRequestException("Email already exists: " + seller.getEmail());
        }

        Seller creatingSeller = modelMapper.map(seller, Seller.class);
        creatingSeller.setId(null);
        return sellerRepository.save(creatingSeller);
    }
}
