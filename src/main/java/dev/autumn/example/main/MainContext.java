package dev.autumn.example.main;

import dev.autumn.annotaion.*;
import dev.autumn.example.common.maintenance.*;
import dev.autumn.*;

@Component
public class MainContext {

	//@OutputContext
	@Autowired("OutputDescriptorV1")
	OutputDescriptor outputHandler;
	
	@Value("${name}")
	private String myName;
	
	@Value("${height}")
	private Integer height;
	
	public MainContext() {
		System.out.println("Hey, I'm MainContext");
	}
	
	
	public void hey() {
		String line = "Hey, I'm MainContext. This is my method hey() " + myName + "  height=" + height;
		System.out.println(line);
		outputHandler.writeline(line);
	}
	
}
