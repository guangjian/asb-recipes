/*******************************************************************************
* Set up a web interface that let's the user suspend/unsuspend the VM
*******************************************************************************/
import org.cloudifysource.dsl.context.ServiceContextFactory
import java.util.concurrent.TimeUnit
import org.cloudifysource.dsl.utils.ServiceUtils;

serviceContext = ServiceContextFactory.getServiceContext()
config = new ConfigSlurper().parse(new File("apacheService-service.properties").toURL())

greetingText=config.WebPageGreeting

webServerDirectory=config.webServerDirectory
webServerCgibin=config.webServerCgibin
webServerHtml=config.webServerHtml

def jbossService = serviceContext.waitForService("jbossService", 300, TimeUnit.SECONDS)
jbossHostInstances = jbossService.waitForInstances(jbossService.numberOfPlannedInstances, 300, TimeUnit.SECONDS)
jbossServerIP = jbossHostInstances[0].hostAddress


println "Jboss Host IP is $jbossServerIP"

builder = new AntBuilder()

builder.sequential {
    echo(message:"apacheService_start.groovy: creating index.html file.")
	replaceregexp(file:"/etc/httpd/conf/workers.properties",
		match:"10.100.1.51",
		replace:"$jbossServerIP")
	exec(executable:"/root/script/load_pool.pl", osfamily:"unix")
    exec(executable: 'service', osfamily:"unix") {
                             arg value:"httpd"
                             arg value:"restart"
    }
}