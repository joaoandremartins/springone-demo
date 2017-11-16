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

@RestController
@RefreshScope
public class WebController {

	@Autowired
	private ApplicationContext context;

	@Autowired
	private HouseRepository houseRepository;

	@Value("${house_id}")
	private long houseId;

	@GetMapping("/")
	public ResponseEntity<Resource> serveImage() {
		House house = houseRepository.findOne(houseId);
		Resource image = context.getResource(house.getGcsPictureAddress());
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.IMAGE_JPEG);
		return new ResponseEntity<>(image, headers, HttpStatus.OK);
	}
}
