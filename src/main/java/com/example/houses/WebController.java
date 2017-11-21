package com.example.houses;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RefreshScope
public class WebController {

	@Autowired
	private ApplicationContext context;

	@Autowired
	private HouseRepository houseRepository;

	@Value("${house_id}")
	private long houseId;

	@GetMapping("/getId")
	public long getId() {
		return houseId;
	}

	@GetMapping("/")
	public ResponseEntity<Resource> serveImage() {
		long fetchedId = new RestTemplate()
				.getForObject("http://localhost:8080/getId",
						Long.class);
		House house = houseRepository.findOne(fetchedId);
		Resource image = context.getResource(house.getGcsPictureAddress());
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.IMAGE_JPEG);
		return new ResponseEntity<>(image, headers, HttpStatus.OK);
	}
}
