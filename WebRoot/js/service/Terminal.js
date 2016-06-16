/**
 * Terminal Service
 * 
 * After (build &) run, a run-time is allocated, pass parameters to open an
 * web-terminal. Then embed to consolePanel.
 */
terminal = {
	dockerid: '',
	port: '',
	lastid: '',
	
	afterInit: function() {
		$("#consolepanel").tabs('disableTab', 'terminal');
		terminal.terminalSelected();
	},

	initTerminal: function(url) {
		$("#terminal").append($("<iframe/>").attr({
			"id": "TerminalFrame",
			"class": "TerminalFrame",
			"src": url,
		}).css({
			width: "100%",
			height: "90%",
			resize: "none"
		}));
	},

	callService: function() {
		var url = Global.url.startTerminal;
		var param = {
			dockerid: terminal.dockerid,
			port: terminal.port
		};
		var success = function(data, status) {
			console.log(data);
			var d = JSON.parse(data);
			if (d.code != 0) {
				consolePanel.append('info', 'Error', d.msg);
			} else {
				terminal.initTerminal(d.url);
				console.log('terminal started successfully');
			}
		};
		$.get(url, param, success);
	},

	errHint: function() {
		alert("Run-time have not started yet. Please (Build&)Run the projct.");
	},

	terminalSelected: function() {
		$("#consolepanel").tabs({
			onSelect: function(title, index) {
				if (title == "terminal") {
					if (terminal.dockerid == undefined || terminal.dockerid == '') {
						console.log('error not boot');
						terminal.errHint();
					} else if (terminal.dockerid != terminal.lastid) {
						terminal.lastid = terminal.dockerid;
						terminal.callService();
					}
					terminal.draggable();
				}

			}
		});
	},

	draggable: function() {
		$("#terminal").height('inherit');
		$("#terminal").parent().height('inherit');
		$("#terminal").parent().parent().height('inherit');
	}

}