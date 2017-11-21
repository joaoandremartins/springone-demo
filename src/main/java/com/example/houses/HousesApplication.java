package com.example.houses;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gcp.pubsub.core.PubSubTemplate;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.WritableResource;
import org.springframework.data.repository.CrudRepository;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.OutputStream;
import java.util.stream.Stream;

@SpringBootApplication
public class HousesApplication {

	public static void main(String[] args) {
		SpringApplication.run(HousesApplication.class, args);
	}

	@Bean
	public CommandLineRunner houses(HouseRepository houseRepository,
			PubSubTemplate pubSubTemplate,
			ApplicationContext context) {
		return args -> {
			Resource internetPicture = context.getResource("https://thenypost.files.wordpress.com/2013/10/google.jpg");
			Resource gcsPicture = context.getResource("gs://springone-houses/google-nyc.jpg");

			byte[] internetPictureBytes = IOUtils.toByteArray(internetPicture.getInputStream());

			try (OutputStream os = ((WritableResource) gcsPicture).getOutputStream()) {
				os.write(internetPictureBytes);
			}

			Resource pivotalPicture = context.getResource("https://www.officelovin.com/" +
					"wp-content/uploads/2016/09/pivotal-labs-office-3.jpg");
			Resource gcsPivotalPicture = context.getResource("gs://springone-houses/pivotal-nyc.png");

			byte[] pivotalBytes = IOUtils.toByteArray(pivotalPicture.getInputStream());

			try (OutputStream os = ((WritableResource) gcsPivotalPicture).getOutputStream()) {
				os.write(pivotalBytes);
			}

			Stream.of(new House("111 8th Av, NYC", gcsPicture.getURI().toString()),
					new House("636 Avenue of Americas, NYC", gcsPivotalPicture.getURI().toString()),
					new House("White House"),
					new House("Pentagon"),
					new House("Empire State Building"))
					.forEach(houseRepository::save);

			houseRepository.findAll()
					.forEach(house -> pubSubTemplate.publish(
							"newHouses", house.getAddress(), null));
		};
	}
}

interface HouseRepository extends CrudRepository<House, Long>{}

@Entity
class House {

	@Id
	@GeneratedValue
	private long id;
	private String address;
	private String gcsPictureAddress;

	public House(String address) {
		this.address = address;
	}

	public House() {
	}

	public House(String address, String gcsPictureAddress) {
		this.address = address;
		this.gcsPictureAddress = gcsPictureAddress;
	}

	public String getGcsPictureAddress() {
		return gcsPictureAddress;
	}

	public void setGcsPictureAddress(String gcsPictureAddress) {
		this.gcsPictureAddress = gcsPictureAddress;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
}
