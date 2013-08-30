
/*******************************************************************************
* Set up a web interface that let's the user suspend/unsuspend the VM
*******************************************************************************/
import org.cloudifysource.dsl.context.ServiceContextFactory

context = ServiceContextFactory.getServiceContext()
config = new ConfigSlurper().parse(new File("mysqlService-service.properties").toURL())

//greetingText=config.WebPageGreeting

//webServerDirectory=config.webServerDirectory
//webServerCgibin=config.webServerCgibin
//webServerHtml=config.webServerHtml

builder = new AntBuilder()

builder.sequential {

	echo(message:"mysqlService_start.groovy: start mysql service.")
	
	//echo(message:"${greetingText}", file:"${webServerDirectory}/${webServerHtml}/index.html")
	exec(executable: 'service', osfamily:"unix") {
							 arg value:"mysqld"
							 arg value:"start"
	}


}
