service {
	
	name "jbossService"

	
	type "WEB_SERVER"
	elastic false
	numInstances 1	
	
	compute {
		template "jbossService_template"
	}	
	
	lifecycle{
 
		start "jbossService_start.groovy"
				
		startDetectionTimeoutSecs 900
		startDetection {			
			println "startDetection: Testing port 8080 ..."
			
			ServiceUtils.isPortOccupied(8080)
		}	
		
		stopDetection {	
			!ServiceUtils.isPortOccupied(8080)
		}
		
			
		// Nothing to locate really.
		locator {	
			return  [] as LinkedList	
			 
        }	
	}
	

	userInterface {
		metricGroups = ([
			metricGroup {
				name "server"

				metrics([
					"Server Uptime",
				])
			}
		])

		widgetGroups = ([
			widgetGroup {
           			name "Server Uptime"
            		widgets ([
               		barLineChart{
                  		metric "Server Uptime"
                  		axisYUnit Unit.REGULAR
							},
            		])
						
			}, 

		])
	}  
}
