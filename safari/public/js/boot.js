require.config({
  paths: {
    jQuery: '/js/lib/jquery-1.9.1',
    Underscore: '/js/lib/underscore-1.5.2',
    Backbone: '/js/lib/backbone-1.1.0',
    text: '/js/lib/text',
    templates: '../templates'
  },

  shim: {
    'Backbone': ['Underscore', 'jQuery'],
    'SocialNet': ['Backbone']
  }
});

require(['SocialNet'], function(SocialNet) {
  SocialNet.initialize();
});