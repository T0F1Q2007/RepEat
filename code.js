document.cookie = "Cookie=SweatTomato";
var date = new Date();
date.setTime(date.getTime() + (365 * 24 * 60 * 60 * 1000));
var expires = "expires=" + date.toUTCString();
document.cookie = "Cookie=chocolate; " + expires;
navigator.serviceWorker.register('/sw.js')
.then(function(registration) {
  console.log('Service worker successfully registered.');
})
.catch(function(error) {
  console.log('Service worker registration failed:', error);
});
Notification.requestPermission().then(function(permission) {
    if (permission === 'granted') {
      console.log('Notification permission granted.');
    } else {
      console.log('Unable to get permission to notify.');
    }
});
navigator.serviceWorker.ready.then(function(registration) {
    registration.pushManager.subscribe({userVisibleOnly: true})
    .then(function(subscription) {
      console.log('Subscribed for push:', subscription.endpoint);
    })
    .catch(function(error) {
      console.log('Subscription failed:', error);
    });
});
self.addEventListener('push', function(event) {
    const title = 'New Message';
    const options = {
      body: 'Hello, world!',
    };
  event.waitUntil(self.registration.showNotification(title, options));
});
