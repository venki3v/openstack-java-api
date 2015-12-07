package com.tdaf.openstack;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.openstack4j.api.Builders;
import org.openstack4j.api.OSClient;
import org.openstack4j.api.types.ServiceType;
import org.openstack4j.model.compute.ActionResponse;
import org.openstack4j.model.compute.FloatingIP;
import org.openstack4j.model.compute.Server;
import org.openstack4j.model.compute.ServerCreate;
import org.openstack4j.model.network.NetFloatingIP;
import org.openstack4j.model.network.Port;
import org.openstack4j.model.network.Subnet;
import org.openstack4j.openstack.OSFactory;

public class openstackcompute {
	
	public static void main(String [] args){
		
		// get network name from openstack cli or horizon
		java.util.List<String> networks = new ArrayList();
		   networks.add("cc812202-f0b4-4b33-b8a0-e0fa21265766");
    // authenticate
		OSClient os = OSFactory.builder()
                .endpoint("http://controller.ecap.com:35357/v2.0")
                .credentials("t3613vs","xxxxxx")
                .tenantName("arch-lab")
                .authenticate();
		
		 System.out.println( "endpoint="+os.getEndpoint());
		 Set<ServiceType> sset =os.getSupportedServices();
		 Iterator iterator = sset.iterator();
		 while(iterator.hasNext()){
		   ServiceType element = (ServiceType)iterator.next();
		   System.out.println("element="+element.getServiceName());
		 }
         
		 //create port example
		 /*Port port = os.networking().port()
	              .create(Builders.port()
	              .name("port1")
	              .networkId("cc812202-f0b4-4b33-b8a0-e0fa21265766")
	              .build());*/
		
	  //list subnet available for the openstack tenant
		 
		/* List<? extends Subnet> subnets = os.networking().subnet().list();
		 int s = 0;
		 while (s < subnets.size()){
			 System.out.println("subnets="+subnets.get(s));
			 s++;
			 
		 }*/
		 String floatingIPID = "";
		 
		 // allocate floatingIP
		 /*List<String> pools = os.compute().floatingIps().getPoolNames();
	        for (String str: pools){
	            FloatingIP ip = os.compute().floatingIps().allocateIP(str);
	            if (ip.getPool().equalsIgnoreCase("nonprod-dcd-net")){
	            	floatingIPID = ip.getFloatingIpAddress();
	            	System.out.println("ipid="+floatingIPID);
	            	System.out.println("ip to string="+ip.toString());
	            }
	            System.out.println("Pool name is: " + ip.getPool());
	            System.out.println("Fixed_IP: "+ ip.getFixedIpAddress());
	            System.out.println("Floating_IP: "+ ip.getFloatingIpAddress());
	        }*/
		 
		    //list floating ip
		 
		 
		 final List<? extends FloatingIP> ips = os.compute().floatingIps().list();
		    for (final FloatingIP ip : ips) {
		        System.out.println(ip);

		        if (ip.getInstanceId() == null) {
		            final String address = ip.getFloatingIpAddress();
		            final String ipID =ip.getId();
		           
		            if (ip.getPool().equalsIgnoreCase("nonprod-dcd-net")){
		            	 System.out.println("get ip address="+address);	
		            	 System.out.println("get ip id"+ipID);	
		              assignFloatingIP(os,ipID,networks);
		              break;
		            } 
		        }
		    }
		   
	        
	    	
		
	}
	private static void assignFloatingIP(final OSClient os, String ipID, List networks) {
		
		// Create a Server Model Object
		
		System.out.println("create server="+ipID);
		 ServerCreate sc = Builders.server()
		                           .name("Rhel7-testnew5")
		                           .flavor("2")
		                           .image("3afd2534-27c1-41d6-bc2e-c94c20bc014f")
		                           .addPersonality("/etc/motd", "Welcome to the new VM! Restricted access only")
		                           .networks(networks)
		                           .build();
		 
		
		
		 NetFloatingIP netFloatingIP = os.networking().floatingip().get(ipID);
		 System.out.println("floatip ipd address from ID="+netFloatingIP.getFloatingIpAddress());
		 //Server server2 = os.compute().servers().get("4698aec8-6047-48b9-b247-2b186d57d2ef");
		// final ActionResponse ar = os.compute().floatingIps().addFloatingIP(server, netFloatingIP.getFloatingIpAddress());

		 // Boot the Server and wait before associate floating ip
		 Server server = os.compute().servers().bootAndWaitActive(sc,100);
		 //Server server = os.compute().servers().boot(sc);
		 System.out.println("serverstatus="); 
		 
		final ActionResponse ar = os.compute().floatingIps().addFloatingIP(server, netFloatingIP.getFloatingIpAddress());
		 System.out.println("serverstatus="+server.getStatus().name()); 
		 //final ActionResponse ar = os.compute().floatingIps().addFloatingIP(server, netFloatingIP.getFloatingIpAddress());
		// System.out.println(ar.isSuccess());
		// System.out.println(ar.getFault());
	    
	}
	

}
