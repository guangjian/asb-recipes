application {
	
	name="threeTiers"
	
	service {
		name = "mysqlService"
	}
	service {
		name = "jbossService"
		dependsOn = ["mysqlService" ]
	}
	
	
	service {
		name = "apacheService"
		dependsOn = ["jbossService" ]
	}
	
	

	
}
