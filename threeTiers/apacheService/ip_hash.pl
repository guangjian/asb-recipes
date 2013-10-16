#!/usr/bin/perl -w 
use strict; 
use Sys::HostAddr; 
use Data::Dumper; 
use Sys::Hostname;
 
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
			printf("$aref->{address}\n"); 
		}
        } 
} 
