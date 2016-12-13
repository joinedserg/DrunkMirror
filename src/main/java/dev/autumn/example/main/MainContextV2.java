package dev.autumn.example.main;

import dev.autumn.annotaion.*;
import dev.autumn.example.common.maintenance.*;
import dev.autumn.*;


@Component("mainContextV2")
public class MainContextV2 {

	@OutputContext("OutputDescriptorV2")
	OutputDescriptor outputHandler;
	
	public MainContextV2() {
		System.out.println("Hey, I'm MainContextV2");
		
	}
	
	
	
}
