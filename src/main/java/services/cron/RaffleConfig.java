
package services.cron;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import services.RaffleService;

@Configuration
@EnableScheduling
public class RaffleConfig {

	@Bean
	public RaffleService toRaffle() {
		return new RaffleService();
	}
	
	
}
