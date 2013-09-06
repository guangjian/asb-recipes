/*******************************************************************************
* Set up a web interface that let's the user suspend/unsuspend the VM
*******************************************************************************/
//import org.cloudifysource.utilitydomain.context.ServiceContextFactory
import org.cloudifysource.dsl.context.ServiceContextFactory
import java.util.concurrent.TimeUnit
import org.cloudifysource.dsl.utils.ServiceUtils;

jbossConfig = new ConfigSlurper().parse(new File("jbossService-service.properties").toURL())

println "jboss_start.groovy: Calculating DBServiceHost..."
serviceContext = ServiceContextFactory.getServiceContext()
instanceID = serviceContext.getInstanceId()
portIncrement = 0

println "jboss_start.groovy: This jboss instance ID is ${instanceID}"
script = "${jbossConfig.home}/bin/standalone"

//Then here's the logic
println "Get mysql server ip ..."
hostIp=InetAddress.localHost.hostAddress
println "Local HostIP1 is: $hostIp"

def mysqlService = serviceContext.waitForService("mysqlService", 300, TimeUnit.SECONDS)
mysqlHostInstances = mysqlService.waitForInstances(mysqlService.numberOfPlannedInstances, 300, TimeUnit.SECONDS)

mysqlServerIP = mysqlHostInstances[0].hostAddress

println "Mysql Host IP is $mysqlServerIP"

println "jboss_start.groovy executing ${script} ..."
new AntBuilder().sequential {
	replaceregexp(file:"/opt/jboss-eap-6.1/standalone/deployments/hello.war/index.jsp",
				match:"10.100.1.91",
				replace:"$mysqlServerIP")
	
	exec(executable:"${script}.sh", osfamily:"unix")
}

println "jboss_start.groovy End of ${script}"
