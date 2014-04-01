class zookeeper {
	$jdk = hiera('jdk.vendor')
	
	include $jdk
	
	package { 'zookeeper':
		ensure => "3.3.5*",
	}
	package { 'zookeeperd':
		ensure => "3.3.5*",
		require => Package["zookeeper"],
	}
}