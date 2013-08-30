/*******************************************************************************
* Copyright (c) 2011 GigaSpaces Technologies Ltd. All rights reserved
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*       http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*******************************************************************************/
import groovy.sql.*

service {
	
	name "mysqlService"

	icon "mysql.png"
	type "DATABASE"
	elastic true
	numInstances 1
	
	compute {
		template "mysqlService_template"
	}	
	
	lifecycle{
 
		start "mysqlService_start.groovy"
				
		startDetectionTimeoutSecs 900
		startDetection {	
			println "mysql alive and mysql jdbc port: ${jdbcPort}"
			ServiceUtils.isPortOccupied(3306)
		}
		
		stopDetection {	
			println "mysql gameover and mysql jdbc port: ${jdbcPort}"
			!ServiceUtils.isPortOccupied(3306)
		}
			
		locator {	
			def myPids = ServiceUtils.ProcessUtils.getPidsWithQuery("State.Name.re=mysql.*\\.exe|mysqld")
			println ":mysqlService-service.groovy: current PIDs: ${myPids}"
			return myPids
        }	
	}
	

	userInterface {
		metricGroups = ([
			metricGroup {
				name "server"

				metrics([
					"Server Uptime",
					"Client Connections",
					"Total Queries",
					"Slow Queries",
					"Opens",
					"Current Open Tables",
					"Queries Per Second"
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
						
         }   , 
			widgetGroup {
           			name "Client Connections"
            		widgets ([
               		barLineChart{
                  		metric "Client Connections"
                  		axisYUnit Unit.REGULAR
							},
            		])
						
         }   , 
			widgetGroup {
           			name "Total Queries"
            		widgets ([
               		barLineChart{
                  		metric "Total Queries"
                  		axisYUnit Unit.REGULAR
							},
            		])
						
         }   , 
			widgetGroup {
           			name "Opens"
            		widgets ([
               		barLineChart{
                  		metric "Opens"
                  		axisYUnit Unit.REGULAR
							},
            		])
						
         }   , 
			widgetGroup {
           			name "Current Open Tables"
            		widgets ([
               		barLineChart{
                  		metric "Current Open Tables"
                  		axisYUnit Unit.REGULAR
							},
            		])
						
         }   , 
			widgetGroup {
           			name "Queries Per Second"
            		widgets ([
               		barLineChart{
                  		metric "Queries Per Second"
                  		axisYUnit Unit.REGULAR
							},
            		])
						
         }   , 
		])
	}  
}
