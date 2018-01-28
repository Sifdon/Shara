var functions = require('firebase-functions');
var request = require('request');
var admin = require('firebase-admin');
var apiKey = 'AIzaSyAyabyhqHXaMJv1P4tlLi-djZHpsV8Epf0';
admin.initializeApp(functions.config().firebase);

var db = admin.database();
var companiesRef = db.ref('companies');
var constantsRef = db.ref('constants');
var statisticsRef = db.ref('statistics');
var usersRef = db.ref('users');

var addressesRef = companiesRef.child('addresses');
var infoRef = companiesRef.child('info');
var goodsRef = companiesRef.child('goods');
var reviewsRef = companiesRef.child('reviews');
var eventsRef = companiesRef.child('events');
var ordersRef = companiesRef.child('orders');
var ratingsRef = companiesRef.child('ratings');
var newsRef = companiesRef.child('news');

var categoriesRef = constantsRef.child('categories');
var citiesRef = constantsRef.child('cities');
var kindsRef = constantsRef.child('kinds');
var operatorsRef = constantsRef.child('operators');
var tagsRef = constantsRef.child('tags');
var typesRef = constantsRef.child('types');
var companiesKeysRef = constantsRef.child('companies');

var eventsStatsRef = statisticsRef.child('events');
var goodsStatsRef = statisticsRef.child('goods');
var newsStatsRef = statisticsRef.child('news');
var companiesStatsRef = statisticsRef.child('companies');

var usersDataRef = usersRef.child('data');

exports.sortShops = functions.https.onRequest((req,res) => {

  var data = req.query;

  var filter = new Object();
  var sort = new Object();

  filter.type = data.type;
  filter.category = data.category;

  sort.origins = data.origins;
  sort.sort = data.sort;
  sort.reverse = data.reverse;

  companiesKeysRef.once('value').then(keysSnap => {
    var allKeys = keysSnap.val();
    var keys = [];

    allKeys.forEach(function(key, index){
      infoRef.child(key + '/type').once('value').then(typeSnap => {
        infoRef.child(key + '/categories').once('value').then(categoriesSnap => {


          if(filter.type == typeSnap.val()) {
            if(filter.category != undefined) {
              var cats = categoriesSnap.val();
              if(cats != undefined){
                cats.forEach(function(cat){
                  if(cat == filter.category) {
                    keys.push(key);
                  }
                });
              }
            } else {
              keys.push(key);
            }
          }


          if(index + 1 == allKeys.length) {
            var array = [];
            keys.forEach(function(key1, index1){
              infoRef.child(key1 + '/name').once('value').then(nameSnap => {
                var obj = new Object();
                obj.key = key1;
                obj.name = nameSnap.val();
                array.push(obj);
                if(index1 + 1 == keys.length) sortShops(sort, array, res);
              });
            });
          }


        });
      });
    });
  });

});

function sortShops(sort, array, res){

  if(sort.sort == 0){

    array.sort(function(a, b){
      if(a.name < b.name) return -1;
      if(a.name > b.name) return 1;
      return 0;
    });

    if(sort.reverse) array.reverse();

    return res.send(JSON.stringify(array));

  } else if(sort.sort == 4){

    keys.forEach(function(key, index){
      infoRef.child(key + '/addresses').once('value').then(addressesKeysSnap => {
        var addressesKeys = addressesKeysSnap.val();
        addressesKeys.forEach(function(addressKey, addressIndex){
          addressesRef.child(key + '/' + addressKey + '/coordinates').once('value').then(coordinatesSnap => {

          });
        });
      });
    });

  } else {

    var ref;
    var field;

    if(sort.sort == 1){
      ref = ratingsRef;
      field = 'company/rating';
    } else if(sort.sort == 2){
      ref = companiesStatsRef;
      field = 'clicks';
    } else if(sort.sort == 3){
      ref = companiesStatsRef;
      field = 'sales';
    }

    array.forEach(function(obj, index){
      ref.child(obj.key + '/' + field).once('value').then(snap => {
        obj.field = snap.val();
        if(index + 1 == array.length) {

          var result = [];

          array.sort(function(a, b){
            if(a.field < b.field) return -1;
            if(a.field > b.field) return 1;
            return 0;
          });

          if(!sort.reverse) array.reverse();

          array.forEach(function(obj){
            delete obj.field;
            result.push(obj);
          });

          return res.send(JSON.stringify(result));
        }
      })
    });
  }
}

exports.getShops = functions.https.onRequest((req,res) => {

  var data = req.query;

  if(data.keys == undefined) return res.send(null);

  var keys = JSON.parse(data.keys);
  var origins = data.origins;
  var shopsMap = new Object();

  var index = 0;

  keys.forEach(function(key){
    infoRef.child(key).once('value').then(infoSnap => {

      var info = infoSnap.val();
      var key = infoSnap.key;

      var shop = new Object();
      shop.name = info.name;

      ratingsRef.child(key + '/company/rating').once('value').then(ratingSnap => {
        shop.rating = ratingSnap.val();

        addressesRef.child(key).once('value').then(addressesSnap => {

          var addresses = addressesSnap.val();

          if(addresses == undefined) {
            index++;
            shopsMap[key] = shop;
            if(index == keys.length)
              return res.send(JSON.stringify(shopsMap));
          } else {
            var aKeys = Object.keys(addresses);
            if(origins == undefined) {
              shop.address = addresses[aKeys[0]];
              delete shop.address['coordinates'];
              delete shop.address['pictures'];

              index++;
              shopsMap[key] = shop;
              if(index == keys.length)
                return res.send(JSON.stringify(shopsMap));

            } else {

              var a = [];
              aKeys.forEach(function(key){
                a.push(addresses[key].coordinates);
              });

              var url = generateDestinations(a, origins);
              request(url, function(error, response, data){

                var times = parseTimes(data);

                var min = Math.min(...times);
                var i = times.indexOf(min);

                shop.address = addresses[aKeys[i]];
                delete shop.address['coordinates'];
                delete shop.address['pictures'];
                shop.address.timeTo = min;

                shopsMap[key] = shop;
                index++;
                if(index == keys.length)
                  return res.send(JSON.stringify(shopsMap));

              });

            }
          }
        });
      });
    });

  });

});

function generateDestinations(addresses, origins){

  var destinations = "";

  addresses.forEach(function(coordinates){
    var lat = coordinates.latitude;
    var lng = coordinates.longitude;
    destinations +=lat+','+lng+'|';
  });

  destinations = destinations.slice(0,destinations.length-1);

  var url = "https://maps.googleapis.com/maps/api/distancematrix/json?" +
    "origins=" + origins + "&" +
    "destinations=" + destinations + "&" +
    "key=" + apiKey;

  return url;

}

function parseTimes(data){

    var times = [];
    var elements = JSON.parse(data).rows[0].elements;

    elements.forEach(function(element){
      times.push(Math.round(element.duration.value/60));
    });

    return times;
}

// exports.searchShop = functions.https.onRequest((req,res) => {
//
//   var name = req.query.name;
//   var type = req.query.type;
//
//   if(names.length == 0){
//     companiesKeysRef.once('value').then(keysSnap => {
//       var keys = keysSnap.val();
//       keys.forEach(function(key, index){
//         infoRef.child(key + '/name').once('value').then(nameSnap => {
//           var obj = new Object();
//           obj.key = key;
//           obj.name = nameSnap.val();
//           names.push(obj);
//           if(index + 1 == keys.length){
//             searchShop(name, res);
//           }
//         });
//       });
//
//     });
//   } else searchShop(name, res);
// });
//
// function searchShop(name, res){
//
//   var result = names.filter(field => field.name.toLowerCase().includes(name.toLowerCase()));
//   return res.send(JSON.stringify(result));
//
// }

exports.getShop = functions.https.onRequest((req,res) => {

  var data = req.query;

  var key = data.key;
  var origins = data.origins;

  infoRef.child(key).once('value').then(infoSnap => {

    var state = 0;
    var info = infoSnap.val();
    var shop = new Object();

    if(info == undefined) return res.send(null);

    var operatorKeys = [];

    shop.key = key;
    shop.name = info.name;
    shop.description = info.description;
    shop.maxPointsPercentage = info.maxPointsPercentage;

    if(info.hasOwnProperty('categories')) shop.categories = [];
    else state++;

    if(info.hasOwnProperty('kinds')) shop.kinds = new Object();
    else state++;

    if(info.hasOwnProperty('contacts')){

      shop.emails = info.contacts.emails;
      shop.sites = info.contacts.sites;

      if(info.contacts.hasOwnProperty('phones')) {
        shop.phones = new Object();
        operatorKeys = Object.keys(info.contacts.phones);
      }
      else state++;

    } else state++;

    operatorKeys.forEach(function(operatorKey, index){
      operatorsRef.child(operatorKey).once('value').then(operatorSnap => {
        var operator = operatorSnap.val();
        shop.phones[operator] = [];
        info.contacts.phones[operatorKey].forEach(function(phone){
          shop.phones[operator].push(phone);
        });
        if(index + 1 == operatorKeys.length) {
          state++;
          if(state == 4) return res.send(JSON.stringify(shop));
        }
      });
    });

    typesRef.child(info.type).once('value').then(typeSnap => {

      shop.type = typeSnap.val();

      if(info.hasOwnProperty('kinds')){
        info.kinds.forEach(function(kindKey, index){
          kindsRef.child(info.type + '/' + kindKey).once('value').then(kindSnap => {
            shop.kinds[kindKey] = kindSnap.val();
            if(index + 1 == info.kinds.length){
              state++;
              if(state == 4) return res.send(JSON.stringify(shop));
            }
          });
        });
      }

      if(info.hasOwnProperty('categories')){
        info.categories.forEach(function(categoryKey, index){
          categoriesRef.child(info.type + '/' + categoryKey).once('value').then(categorySnap => {
            shop.categories.push(categorySnap.val());
            if(index + 1 == info.categories.length){
              state++;
              if(state == 4) return res.send(JSON.stringify(shop));
            }
          });
        });
      }
    });

    ratingsRef.child(key).once('value').then(ratingSnap => {
      var ratingData = ratingSnap.val();
      if(ratingData != undefined){
        shop.rating = ratingData.company.rating;
        shop.points = ratingData.company.points;

        addressesRef.child(key).once('value').then(addressesSnap => {

          shop.addresses = addressesSnap.val();

          var ratingAddressesData = ratingData.addresses;
          Object.keys(shop.addresses).forEach(function(addressKey){
            if(ratingAddressesData.hasOwnProperty(addressKey)){
              shop.addresses[addressKey].rating = ratingAddressesData[addressKey].rating;
              shop.addresses[addressKey].points = ratingAddressesData[addressKey].points;
            }
          });

          if(origins != undefined && shop.addresses != undefined) {
            state--;

            var a = [];
            Object.keys(shop.addresses).forEach(function(key){
              a.push(shop.addresses[key].coordinates);
            });

            var url = generateDestinations(a, origins);
            request(url, function(error, response, data){

              var times = parseTimes(data);

              Object.keys(shop.addresses).forEach(function(key, index){
                shop.addresses[key].timeTo = times[index];
              });

              state++;
              if(state == 4) return res.send(JSON.stringify(shop));

            });

          }

          state++;
          if(state == 4) return res.send(JSON.stringify(shop));

        });
      } else {
        state++;
        if(state == 4) return res.send(JSON.stringify(shop));
      }
    });

  });
});

exports.sortGoods = functions.https.onRequest((req,res) => {

  var data = req.query;

  var sort = new Object();
  sort.key = data.key;
  sort.kind = data.kind;
  sort.reverse = data.reverse;
  sort.sort = data.sort;

  infoRef.child(sort.key+'/goods/'+sort.kind).once('value').then(goodsKeysSnap => {
    var keys = goodsKeysSnap.val();
    var array = [];
    keys.forEach(function(key, index){
      goodsRef.child(sort.key + '/' + sort.kind + '/' + key + '/name').once('value').then(nameSnap => {
        var obj = new Object();
        obj.key = key;
        obj.name = nameSnap.val();
        array.push(obj);
        if(index + 1 == keys.length) sortGoods(sort, array, res);
      });
    });
  });
});

function sortGoods(sort, array, res){

  if(sort.sort == 0){

    array.sort(function(a, b){
      if(a.name < b.name) return -1;
      if(a.name > b.name) return 1;
      return 0;
    });

    if(sort.reverse) array.reverse();

    return res.send(JSON.stringify(array));

  } else {

    var ref;
    var field;

    if(sort.sort == 1){
      ref = goodsRef.child(sort.key + '/' + sort.kind);
      field = 'price';
    }

    array.forEach(function(obj, index){
      ref.child(obj.key + '/' +field).once('value').then(snap => {
        obj.field = snap.val();
        if(index + 1 == keys.length){

          var result = [];

          array.sort(function(a, b){
            if(a.field < b.field) return -1;
            if(a.field > b.field) return 1;
            return 0;
          });

          if(!sort.reverse) array.reverse();

          array.forEach(function(obj){
            delete obj.field;
            result.push(obj);
          });

          return res.send(JSON.stringify(result));
        }
      });
    });
  }
}

// exports.sortReviews = functions.https.onRequest((req,res) => {
//     var key = req.query.key;
//
//     admin.database().ref('companies/reviews/'+key).once('value').then(reviews => {
//         var result = [];
//         reviews.forEach(function(review){
//             result.push(review.key);
//         });
//         res.send(JSON.stringify(result));
//     });
// });
//
//
// exports.getReviews = functions.https.onRequest((req,res) => {
//
//     var key = req.query.key;
//     var address = req.query.address;
//     admin.database().ref('companies/reviews/'+key).once('value').then(reviewsSnapshot=>{
//         var result = [];
//         return res.send(JSON.stringify(reviewsSnapshot.val()));
//
//     });
// });
//
//
// exports.reviewAdded = functions.database.ref('companies/reviews/{companyKey}/{reviewKey}').onWrite(event => {
//
//     var cKey = event.params.companyKey;
//     var rKey = event.params.reviewKey;
//
//     var reviewSnapshot = event.data;
//
//     console.log(reviewSnapshot);
//
//     var review = event.data.val();
//
// });
