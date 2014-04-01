class openjdk {
	$jdk_version = hiera('jdk.version')
	
	package { "openjdk$jdk_version":
		name => "openjdk-$jdk_version-jdk",
		ensure => installed,
	}
	
	exec { "update-java-alternatives":
		command => "update-java-alternatives -s java-1.$jdk_version.0-openjdk-i386",
		path => "/usr/sbin:/usr/bin/:/bin/",
		require => Package["openjdk$jdk_version"],
	}
	
}