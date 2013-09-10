import java.util.concurrent.TimeUnit
import org.cloudifysource.dsl.context.ServiceContextFactory
import org.cloudifysource.dsl.utils.ServiceUtils;

service {
    name "apacheService"
    type "WEB_SERVER"
    
    /*
    elastic "${ScalingOn}"
    numInstances "${BaseNumberOfDnsServers}"
    minAllowedInstances "${BaseNumberOfDnsServers}"
    maxAllowedInstances "${MaxAllowedDnsServers}"
    */
    elastic true
    numInstances 1
    minAllowedInstances 1
    maxAllowedInstances 2

    //get cpu usage from
    /*
    serviceContext = ServiceContextFactory.getServiceContext()
    config = new ConfigSlurper().parse(new File("apacheService-service.properties").toURL())
    minCpuUsage = config.minCpuUsage
    maxCpuUsage = config.maxCpuUsage

    println "minCpuUsage"
    println "${minCpuUsage}"
    println "maxCpuUsage"
    println "${maxCpuUsage}"
    */



    compute {
        template "apacheService_template"
    }    
    
    lifecycle{
		
		monitors {
			value="/root/script/cpu_usage.sh".execute().text
			println "Tier One CPU USAGE: ${value}"
			return ["CPU USAGE":value as Float]
		}
    
        start "apacheService_start.groovy"
                
        startDetectionTimeoutSecs 900
        startDetection {            
            ServiceUtils.isPortOccupied(80)
        }    
        
        
        stopDetection {    
            !ServiceUtils.isPortOccupied(80)
        }
         
            
        // Nothing to locate really.
        locator {    
            //return  [] as LinkedList
			
			def myPids = ServiceUtils.ProcessUtils.getPidsWithQuery("State.Name.re=httpd|apache")
			println "apache-service.groovy: current PIDs: ${myPids}"
			return myPids
			
        }    
    }
    

    userInterface {

        metricGroups = ([
            metricGroup {
				name "process"
				metrics([
					"Total Process Cpu Time",
					"CPU USAGE",
					
				])
            }
        ])


        widgetGroups = ([

			widgetGroup {
				name "Total Process Cpu Time"
				widgets([
					balanceGauge{metric = "Total Process Cpu Time"},
					barLineChart {
						metric "Total Process Cpu Time"
						axisYUnit Unit.REGULAR
					},
				])
			} ,
			
			widgetGroup {
					name "CPU USAGE"
					widgets ([
						balanceGauge{metric = "CPU USAGE"},
						barLineChart{
							metric "CPU USAGE"
							axisYUnit Unit.REGULAR
						},
					])
			} ,
		])

	}
    
    // Once additional VMs have been added or removed (scaling has occured), the scaling rules will
    // be disabled this number of seconds.

    scaleCooldownInSeconds 30
    samplingPeriodInSeconds 1




    scalingRules ([
        scalingRule {

            serviceStatistics {
				metric "CPU USAGE"
				Statistics.maximumOfAverages
				movingTimeRangeInSeconds 20
            }
            

            highThreshold {
                value 10
                instancesIncrease 1
                
            }


            lowThreshold {
                value 5
                instancesDecrease 1
            }
            
        }
    ])  
}