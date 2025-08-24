package ssi.backend.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ssi.backend.dtos.RegisterRequestDTO;
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

    public List<String> getAllBuyerEmails() {
        return buyerRepository.findAllEmailsBuyer();
    }

    public List<String> getAllSellerEmails() {
        return sellerRepository.findAllEmailsSeller();
    }

    public Buyer createBuyer(RegisterRequestDTO buyer) {
        if (getAllBuyerEmails().contains(buyer.getEmail()) ||
                getAllSellerEmails().contains(buyer.getEmail())) {
            throw new BadRequestException("Email already exists: " + buyer.getEmail());
        }

        Buyer creatingBuyer = new Buyer();
        creatingBuyer.setNickname(buyer.getNickname());
        creatingBuyer.setEmail(buyer.getEmail());
        creatingBuyer.setPassword(buyer.getPassword());
        creatingBuyer.setFullname(buyer.getFullname());
        return buyerRepository.save(creatingBuyer);

    }

    public Seller createSeller(RegisterRequestDTO seller) {
        if (getAllBuyerEmails().contains(seller.getEmail()) ||
                getAllSellerEmails().contains(seller.getEmail())) {
            throw new BadRequestException("Email already exists: " + seller.getEmail());
        }

        Seller creatingSeller = modelMapper.map(seller, Seller.class);
        return sellerRepository.save(creatingSeller);
    }
}
