#!/usr/bin/perl -w
# v1.0
use strict;
use Net::Telnet;
use Sys::HostAddr;

use Socket;
use Sys::Hostname;


#login info
my $host        = 'localhost';
my $vtyPass     = 'admin';
my $enaPass     = 'ena123';
my $cmd         = ' /info/slb/dump';
my $group_id	= 'localgroup';

my $enable  = 'enable';
my $real_ip = '192.168.2.1';
my $real_id = '1';



$real_ip = get_hash_ip();
$real_id = substr($real_ip, 10);
my $cmd_real_ip = "/c/slb/real ".$real_id;
my $wait_real_ip = sprintf("/Real Server %d/i\n", $real_id);
my $cmd_add_ip = " add ".$real_id;
my $cmd_ena_ip = "ena ".$real_id;

my $cmd_group_id = "/c/slb/group ".$group_id;
my $wait_group_id = sprintf("/Real Server Group %s/i\n", $group_id);

printf "Definition parameter...\n";
printf "Real IP %s\n", $real_ip;
printf "Real ID %s\n", $real_id;
printf "CMD Real IP:%s\n", $cmd_real_ip;
printf "CMD wait real IP:%s\n", $wait_real_ip;
printf "CMD add ip:%s\n", $cmd_add_ip;
printf "CMD ena ip:%s\n", $cmd_ena_ip;
printf "CMD group id:%s\n",$group_id;



print "Staring add VM to Alteon VA\n";

# set vm default route
system "route del default";
system "route add default gw $host";

#connect telnet server
my $conn = new Net::Telnet(
    Timeout => 60,
);
$conn->open($host);


#vty password
$conn->waitfor('/password/i');
$conn->print($vtyPass);

my @output;

# add real server
$conn->waitfor('/Main/i');
@output = $conn->print($cmd_real_ip);
printf "Output:%s\n", $cmd_real_ip;
sleep 2;

# $conn->waitfor('/Real Server 14/i');
$conn->waitfor($wait_real_ip);
@output = $conn->print(' ena');
print "Output:ena\n";

sleep 2;
$conn->waitfor($wait_real_ip);
@output = $conn->print('ipver v4');
print "Output:ipver v4\n";

sleep 2;
$real_ip = "rip ".$real_ip;
print "Get Real IP: $real_ip\n";
$conn->waitfor($wait_real_ip);
@output = $conn->print($real_ip);

sleep 2;

$conn->waitfor($wait_real_ip);
@output = $conn->print('name "apache"');
printf "Output:name apache\n";

sleep 2;
$conn->waitfor($wait_real_ip);
@output = $conn->print($cmd_group_id);
printf "Output:/c/slb/group %s\n", $group_id;

sleep 2;
$conn->waitfor($wait_group_id);
@output = $conn->print($cmd_add_ip);
printf "Output:%s\n", $cmd_add_ip;

sleep 2;
$conn->waitfor($wait_group_id);
@output = $conn->print($cmd_ena_ip);
printf "Output:%s\n", $cmd_ena_ip;

sleep 2;
$conn->waitfor($wait_group_id);
@output = $conn->print('apply');
print "Output:apply\n";

$conn->waitfor($wait_group_id);
@output = $conn->print('save');
print "Output:save\n";

$conn->waitfor('/Confirm saving to FLASH/i');
@output = print "Output:Confirm saving to FLASH\n";

$conn->print('y');
@output = print "Output:y\n";

#disconnect
$conn->close;
print "Output:disconnect\n";


# Get real ip address for which connnect to Alteon VA
sub get_real_ip {
	my $host = hostname();
	#print $host."\n";

	my $name;
	my $aliases;
	my $type;
	my $len;
	my $ip;
	my $pool_ip;
	my @thisaddr;
	my $position;
	($name,$aliases,$type,$len,@thisaddr)=gethostbyname($host);

	foreach(@thisaddr)
	{
	   $ip = inet_ntoa($_)."\n";
	   $position = index($ip, "192.168.20");
	   if ($position == 0) {
		   return $ip;
	   }
	}
}

sub get_hash_ip {
	my $sysaddr=Sys::HostAddr->new();

	my $ip_addr=$sysaddr->ip();
	#print Dumper($ip_addr);
	foreach my $interface(keys %{$ip_addr})
	{
		foreach my $aref(@{$ip_addr->{$interface}})
		{
			#printf("$interface  $aref->{address}\n");
			#print Dumper($aref);
			my $ip = $aref->{address};
			my $position = index($ip, "192.168.2");
			if ($position == 0) {
				return $ip
			}
		}
	}
}

