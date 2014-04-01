class oracle-jdk {
	$jdk_version = hiera('jdk.version')
	
	package { "oracle-jdk$jdk_version":
		name => "oracle-java$jdk_version-installer",
		responsefile => "./modules/oracle-jdk/files/jdk$jdk_version.seeds",
		ensure => installed,
		require => Exec["apt-get-update"],
	}

	package { 'python-software-properties':
		ensure => installed,
	}

	exec { "apt-add-java-ppa":
		command => "add-apt-repository ppa:webupd8team/java",
		path    => "/usr/bin/",
		require => Package["python-software-properties"],
	}

	exec { "add-ppa-key":
		command => "apt-key adv --keyserver keyserver.ubuntu.com --recv-keys EEA14886",
		path	=> "/bin:/usr/bin",
		require => Exec["apt-add-java-ppa"],
	}

	exec { "apt-get-update":
		command => "apt-get update",
		path    => "/usr/bin/",
		require => Exec["add-ppa-key"],
	}
}



# class oracle-jdk {
# 	package { 'oracle-jdk6':
# 		name => 'oracle-java6-installer',
# 		responsefile => './modules/oracle-jdk6/files/jdk6.seeds',
# 		ensure => installed,
# 		require => Exec["apt-get-update"],
# 	}
# 
# 	package { 'python-software-properties':
# 		ensure => installed,
# 	}
# 
# 	exec { "apt-add-java-ppa":
# 		command => "add-apt-repository ppa:webupd8team/java",
# 		path    => "/usr/bin/",
# 		require => Package["python-software-properties"],
# 	}
# 
# 	exec { "add-ppa-key":
# 		command => "apt-key adv --keyserver keyserver.ubuntu.com --recv-keys EEA14886",
# 		path	=> "/bin:/usr/bin",
# 		require => Exec["apt-add-java-ppa"],
# 	}
# 
# 	exec { "apt-get-update":
# 		command => "apt-get update",
# 		path    => "/usr/bin/",
# 		require => Exec["add-ppa-key"],
# 	}
# }
