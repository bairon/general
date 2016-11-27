package alsa;

import alsa.general.Dealer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class GeneralApplication {

	@Autowired
	Dealer dealer;

	@PostConstruct
	public void init() {
		new Thread(dealer).start();
	}


		public static void main(String[] args) {
		SpringApplication.run(GeneralApplication.class, args);
	}
}
