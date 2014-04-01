class storm-zeromq {
	$zeromq_version = hiera('zeromq.version')
	$zeromq_binary = "${zeromq_version}.tar.gz"
	$jdk_vendor = hiera('jdk.vendor')
	$jdk_version = hiera('jdk.version')
	
	# if ($jdk_vendor == 'oracle-jdk') {
	# 	#$java_home = "/usr/lib/jvm/java-$jdk_version-oracle"
	# }
	# elseif ($jdk_vendor == 'openjdk') {
	# 	#$java_home = "/usr/lib/jvm/java-$jdk_version-openjdk-i386/"
	# }

	$java_home = $jdk_vendor ? {
		oracle-jdk => "/usr/lib/jvm/java-$jdk_version-oracle",
		openjdk => "/usr/lib/jvm/java-$jdk_version-openjdk-i386",
	}
	package { 'uuid-dev':
		ensure => installed,
	}
	
	package { 'libtool':
		ensure => installed,
	}
	
	package { 'autoconf':
		ensure => installed,
	}
	
	
	package { 'git':
	  ensure => installed,
	}
	
	exec { "zeromq-download":
		command => "wget -N http://download.zeromq.org/$zeromq_binary",
		path => "/usr/bin/:/bin/",
		cwd => "/tmp/",
		require => Package["uuid-dev"],
		unless => "test -f /tmp/$zeromq_binary",
	}
	exec { "zeromq-unpack":
		command => "tar -xvzf $zeromq_binary",
		path => "/usr/bin/:/bin/",
		cwd => "/tmp/",
		require => Exec["zeromq-download"],
		unless => "test -d /tmp/$zeromq_version" 
	}
	exec { "zeromq-build":
		command => "configure && make && make install",
		path => "/bin/:/usr/bin/:/tmp/$zeromq_version/",
		cwd => "/tmp/$zeromq_version",
		require => Exec["zeromq-unpack"],
		unless => "test -f /usr/local/lib/libzmq.so" 
	}
	
	package { "pkg-config":
		ensure => installed,
	}
	
	exec { "jzmq-clone":
		command => "git clone http://github.com/nathanmarz/jzmq.git",
		path => "/usr/bin/:/bin/",
		cwd => "/tmp/",
		returns => ["0","128"],
		require => [Package["git"], Exec["zeromq-build"]],
	}
	
	# https://github.com/zeromq/jzmq/issues/114
	exec {"jzmq-patch":
		command => "sed -i 's/classdist_noinst.stamp/classnoinst.stamp/g' src/Makefile.am",
		path => "/bin/",
		cwd => "/tmp/jzmq",
		require => Exec["jzmq-clone"],
	}
	
	# /usr/lib/jvm/java-7-openjdk-i386/
	exec { "jzmq-build":
		command => "autogen.sh && configure && make && make install",
		environment => [ "JAVA_HOME=$java_home"],
		path => "/bin/:/usr/bin/:/tmp/jzmq/",
		cwd => "/tmp/jzmq",
		alias => "build jzmq",
		require => [Exec["jzmq-clone"],Exec["jzmq-patch"],Package["pkg-config"],Exec["zeromq-build"], Package["$jdk_vendor$jdk_version"]],
		unless => "test -f /usr/local/lib/libjzmq.so" 
	}
}