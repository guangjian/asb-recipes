/*******************************************************************************
* Set up a web interface that let's the user suspend/unsuspend the VM
*******************************************************************************/
import org.cloudifysource.dsl.context.ServiceContextFactory
import java.util.concurrent.TimeUnit
import org.cloudifysource.dsl.utils.ServiceUtils
import groovy.util.ConfigSlurper

serviceContext = ServiceContextFactory.getServiceContext()
config = new ConfigSlurper().parse(new File("apacheService-service.properties").toURL())

def jbossService = serviceContext.waitForService("jbossService", 300, TimeUnit.SECONDS)
jbossHostInstances = jbossService.waitForInstances(jbossService.numberOfPlannedInstances, 300, TimeUnit.SECONDS)
jbossServerIP = jbossHostInstances[0].hostAddress
alteonVAIP = config.AlteonVAIP


println "Jboss Host IP is $jbossServerIP"
println "Alteon MNG IP"
println "${alteonVAIP}"

builder = new AntBuilder()

builder.sequential {
    echo(message:"apacheService_start.groovy: creating index.html file.")

    //indicate Alteon VA Management IP from recipe parameter
    copy(file:"${serviceContext.serviceDirectory}/${config.AlteonVAScript}" , tofile:"/root/script/${config.AlteonVAScript}")
    chmod(dir:"/root/script", perm:"+x", includes:"*.pl")

    //indicate jboss ip in apache JK2
	replaceregexp(file:"/etc/httpd/conf/workers.properties",
		match:"10.100.1.51",
		replace:"$jbossServerIP")

    replaceregexp(file:"/root/script/load_pool.pl",
            match:"localhost",
            replace:"${alteonVAIP}")

	exec(executable:"/root/script/load_pool.pl", osfamily:"unix")
    exec(executable: 'service', osfamily:"unix") {
                             arg value:"httpd"
                             arg value:"restart"
    }
}