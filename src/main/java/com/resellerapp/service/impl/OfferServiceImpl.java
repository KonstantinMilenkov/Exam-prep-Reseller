package com.resellerapp.service.impl;

import com.resellerapp.model.OfferCreateBindingModel;
import com.resellerapp.model.dto.BoughtOffersDTO;
import com.resellerapp.model.dto.MyOfferDTO;
import com.resellerapp.model.dto.OfferHomeDTO;
import com.resellerapp.model.dto.OtherOffersDTO;
import com.resellerapp.model.entity.Condition;
import com.resellerapp.model.entity.Offer;
import com.resellerapp.model.entity.User;
import com.resellerapp.repository.ConditionRepository;
import com.resellerapp.repository.OfferRepository;
import com.resellerapp.repository.UserRepository;
import com.resellerapp.service.LoggedUser;
import com.resellerapp.service.OfferService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OfferServiceImpl implements OfferService {
    private final OfferRepository offerRepository;
    private final UserRepository userRepository;
    private final ConditionRepository conditionRepository;
    private final LoggedUser loggedUser;

    public OfferServiceImpl(OfferRepository offerRepository, UserRepository userRepository, ConditionRepository conditionRepository, LoggedUser loggedUser) {
        this.offerRepository = offerRepository;
        this.userRepository = userRepository;
        this.conditionRepository = conditionRepository;
        this.loggedUser = loggedUser;
    }

    @Override
    public OfferHomeDTO getOffersForHomePage() {
        List<Offer> offers = offerRepository.findAll();

        List<MyOfferDTO> myOffers = new ArrayList<>();
        List<BoughtOffersDTO> boughtOffers = new ArrayList<>();
        List<OtherOffersDTO> otherOffers = new ArrayList<>();

        for (Offer offer : offers) {
            String loggedUsername = loggedUser.getUsername();

            if (offer.getCreatedBy().getUsername().equals(loggedUsername)) {
                myOffers.add(new MyOfferDTO(offer));
            } else if (offer.getBoughtBy().getUsername().equals(loggedUsername)) {
                boughtOffers.add(new BoughtOffersDTO(offer));
            } else {
                otherOffers.add(new OtherOffersDTO(offer));
            }
        }

        return new OfferHomeDTO(myOffers, boughtOffers, otherOffers);
    }

    @Override
    public boolean create(OfferCreateBindingModel offerCreateBindingModel) {
        Condition condition = conditionRepository.findByName(offerCreateBindingModel.getCondition());

        User user = userRepository.findByUsername(loggedUser.getUsername());

        if (condition != null && user != null){
            Offer offer = new Offer(offerCreateBindingModel, condition, user);

            offerRepository.save(offer);
            return true;
        }

        return false;
    }
}
