package ru.otus.hw.service;


import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import ru.otus.hw.domain.Butterfly;
import ru.otus.hw.domain.Larva;

import java.util.Collection;

@MessagingGateway
public interface LifeCycleGateway {

	@Gateway(requestChannel = "larvaChannel", replyChannel = "butterflyChannel")
	Collection<Butterfly> process(Collection<Larva> larvae);

}
