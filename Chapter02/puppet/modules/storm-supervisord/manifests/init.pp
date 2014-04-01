class storm-supervisord {

	package { 'supervisor':
	  ensure => installed,
	}
	
	file { "storm-ui-conf":
		path => "/etc/supervisor/conf.d/storm-ui.conf",
		ensure => file,
		content => template("storm-supervisord/storm-ui.conf.erb"),
		require => Package['supervisor'],
		notify => Service['supervisor'],
	}
	
	file { "storm-nimbus-conf":
		path => "/etc/supervisor/conf.d/storm-nimbus.conf",
		ensure => file,
		content => template("storm-supervisord/storm-nimbus.conf.erb"),
		require => Package['supervisor'],
		notify => Service['supervisor'],
	}
	
	file { "storm-supervisor-conf":
		path => "/etc/supervisor/conf.d/storm-supervisor.conf",
		ensure => file,
		content => template("storm-supervisord/storm-supervisor.conf.erb"),
		require => Package['supervisor'],
		notify => Service['supervisor'],
	}
	service { "supervisor":
		ensure => running,
		enable => true,
		hasrestart => false,
		hasstatus => false,
	}
}