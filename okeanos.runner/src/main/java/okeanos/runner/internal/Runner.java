package okeanos.runner.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

import okeanos.platform.PlatformService;

/**
 * Internal implementation of our example OSGi service
 */
public class Runner
{

private PlatformService platformService;

public PlatformService getPlatformService() {
return platformService;
}

public void setPlatformService(PlatformService plat) {
platformService = plat;
System.out.println("Hurray 1!");
System.out.println(platformService.scramble("ABCDEFGHIJKLMOPQRSTUVWXYZ"));
System.out.println("Hurray 2!");
}

@PostConstruct
public void main() {
System.out.println("Hurray 1!");
System.out.println(platformService);
System.out.println("Hurray 2!");

}

}

