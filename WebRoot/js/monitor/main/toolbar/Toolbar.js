topBar = {

	menuItems : [],
	customHandler : [],

	initMenuItems : function() {
	},

	InitTopBar : function() {
		var toolbar = $('#toolbar');
		toolbar.attr({
			"region" : "north",
			"noheader" : true,
			"border" : false
		}).css({
			"background-color" : "rgb(230, 230, 230)"
		});

		toolbar
				.append('<span class="datagrid-btn-separator" style="vertical-align: middle; height: 15px;display:inline-block;float:none"></span>');

		// Menu end
		toolbar.append($("<br/>"));

		toolbar.append($("<a/>").attr({
			"href" : "#",
			"id" : "startReplay"
		}).linkbutton({
			plain : true,
			iconCls : 'run'
		}).bind('click', Global.onStartReplay));
		$("#StartReplayButton").tooltip({
			position : "bottom",
			content : "Start Replay"
		}).addClass("quickmenu");

		toolbar.append($("<a/>").attr({
			"href" : "#",
			"id" : "pauseReplay"
		}).linkbutton({
			plain : true,
			iconCls : 'suspend'
		}).bind('click', Global.onPauseReplay));
		$("#PauseReplayButton").tooltip({
			position : "bottom",
			content : "Pause Replay"
		}).addClass("quickmenu");

		toolbar.append($("<a/>").attr({
			"href" : "#",
			"id" : "stopReplay"
		}).linkbutton({
			plain : true,
			iconCls : 'terminate'
		}).bind('click', Global.onStopReplay));
		$("#StopReplayButton").tooltip({
			position : "bottom",
			content : "Stop Replay"
		}).addClass("quickmenu");

		toolbar.append($("<a />").attr({
			"href" : "#",
			"id" : "ReplaySlider"
		}));
		$("#ReplaySlider").slider({
        	"width": 400,
		    "max": 100,
		    "onComplete" : Global.onChangeSlider
		});
		return this;
	},

	customMenuItems : function(menuTitle, prependItems, appendItems) {
		for (i in this.menuItems) {
			if (this.menuItems[i].Menu[0].id == menuTitle + "Menu") {
				var menu = this.menuItems[i].Menu;
				for (i in prependItems) {
					menu.prepend(prependItems[prependItems.length - i - 1]);
				}
				for (i in appendItems) {
					menu.append(appendItems[i]);
				}
			}
		}
	},

};