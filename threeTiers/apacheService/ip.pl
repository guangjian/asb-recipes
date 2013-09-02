use strict; 
use Socket; 
use Sys::Hostname; 

my $host = hostname(); 
print $host."\n"; 

my $name; 
my $aliases; 
my $type; 
my $len; 
my $ip;
my $pool_ip;
my $real_id;
my @thisaddr; 
my $position;
($name,$aliases,$type,$len,@thisaddr)=gethostbyname($host); 

foreach(@thisaddr) 
{ 
   $ip = inet_ntoa($_)."\n";
   $position = index($ip, "192.168.20");
   if ($position == 0) {
	   print $ip;
	   $real_id = substr($ip, 11);
	   print $real_id;
   }
}
