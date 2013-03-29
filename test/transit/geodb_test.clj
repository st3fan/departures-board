(ns transit.geodb-test
  (:use
    clojure.test
    transit.geodb)
  (:require
    [transit.geodb :as geodb]))

(def stops [{:longitude -79.54412, :title "Long Branch Loop", :latitude 43.5918099, :tag "1750"}
            {:longitude -79.5406699, :title "Lake Shore Blvd West At Thirty Ninth St", :latitude 43.5928, :tag "5110"}
            {:longitude -79.53822, :title "Lake Shore Blvd West At Thirty Seventh St", :latitude 43.5933499, :tag "1073"}
            {:longitude -79.5340999, :title "Lake Shore Blvd West At Long Branch Ave", :latitude 43.59432, :tag "4736"}
            {:longitude -79.5303799, :title "Lake Shore Blvd West At Thirty First St", :latitude 43.59513, :tag "1719"}
            {:longitude -79.5277099, :title "Lake Shore Blvd West At Twenty Eighth St", :latitude 43.5957, :tag "1233"}
            {:longitude -79.5250399, :title "Lake Shore Blvd West At Twenty Seventh St", :latitude 43.59633, :tag "2957"}
            {:longitude -79.52141, :title "Lake Shore Blvd West At Twenty Third St", :latitude 43.5971299, :tag "4779"}
            {:longitude -79.51717, :title "Lake Shore Blvd West At Colonel Samuel Smith Park Dr", :latitude 43.5980699, :tag "7984"}
            {:longitude -79.5143999, :title "Lake Shore Blvd West At Fifteenth St", :latitude 43.59868, :tag "5188"}
            {:longitude -79.51196, :title "Lake Shore Blvd West At Thirteenth St", :latitude 43.5992099, :tag "5479"}
            {:longitude -79.5088199, :title "Lake Shore Blvd West At Tenth St", :latitude 43.5999, :tag "5050"}
            {:longitude -79.5054, :title "Lake Shore Blvd West At Seventh St", :latitude 43.6006599, :tag "3370"}
            {:longitude -79.5031099, :title "Lake Shore Blvd West At Fifth St", :latitude 43.60116, :tag "7245"}
            {:longitude -79.50087, :title "Lake Shore Blvd West At Third St", :latitude 43.6016499, :tag "7612"}
            {:longitude -79.4986899, :title "Lake Shore Blvd West At First St", :latitude 43.60213, :tag "6435"}
            {:longitude -79.49324, :title "Lake Shore Blvd West At Royal York Rd", :latitude 43.6035199, :tag "7847"}
            {:longitude -79.4903699, :title "Lake Shore Blvd West At Lake Cres", :latitude 43.60684, :tag "8158"}
            {:longitude -79.4902499, :title "Lake Shore Blvd West At Miles Rd", :latitude 43.6082, :tag "1299"}
            {:longitude -79.4899499, :title "Lake Shore Blvd West At Norris Cres", :latitude 43.61082, :tag "8173"}
            {:longitude -79.48977, :title "Lake Shore Blvd West At Summerhill Rd", :latitude 43.6120599, :tag "4004"}
            {:longitude -79.48929, :title "Lake Shore Blvd West At Mimico Ave", :latitude 43.61358, :tag "2173"}
            {:longitude -79.48862, :title "Lake Shore Blvd West At Superior Ave", :latitude 43.6148199, :tag "5516"}
            {:longitude -79.4873, :title "Lake Shore Blvd West At Burlington St", :latitude 43.6171599, :tag "8764"}
            {:longitude -79.48648, :title "Lake Shore Blvd West At Louisa St", :latitude 43.6188199, :tag "10217"}
            {:longitude -79.4833399, :title "Lake Shore Blvd West At Legion Rd", :latitude 43.62035, :tag "5692"}
            {:longitude -79.4811999, :title "Lake Shore Blvd West At Park Lawn Rd", :latitude 43.62278, :tag "9453"}
            {:longitude -79.4796999, :title "2155 Lake Shore Blvd West", :latitude 43.62573, :tag "2061"}
            {:longitude -79.47806, :title "2111 Lake Shore Blvd West", :latitude 43.6290199, :tag "9423"}
            {:longitude -79.47854, :title "Humber Loop", :latitude 43.6310899, :tag "3576"}
            {:longitude -79.47341, :title "The Queensway At South Kingsway", :latitude 43.63528, :tag "7813"}
            {:longitude -79.4689699, :title "The Queensway At Windermere Ave East Side", :latitude 43.6372, :tag "10266"}
            {:longitude -79.4655599, :title "The Queensway At Ellis Ave East Side", :latitude 43.63784, :tag "6256"}
            {:longitude -79.4585599, :title "The Queensway At Colborne Lodge Dr East Side", :latitude 43.63943, :tag "8901"}
            {:longitude -79.4541399, :title "The Queensway At Parkside Dr", :latitude 43.63955, :tag "4236"}
            {:longitude -79.4508499, :title "The Queensway At Glendale Ave East Side (St Josephs Hosp)", :latitude 43.6390799, :tag "8941"}
            {:longitude -79.44646, :title "The Queensway At Roncesvalles Ave", :latitude 43.6385699, :tag "4321"}
            {:longitude -79.4426299, :title "Queen St West At Wilson Park Rd", :latitude 43.63936, :tag "8722"}
            {:longitude -79.4399899, :title "Queen St West At Dowling Ave", :latitude 43.63989, :tag "4032"}
            {:longitude -79.43752, :title "Queen St West At Jameson Ave", :latitude 43.6403899, :tag "10323"}
            {:longitude -79.4346899, :title "Queen St West At Dunn Ave", :latitude 43.64096, :tag "2136"}
            {:longitude -79.4324199, :title "Queen St West At Brock Ave", :latitude 43.6414, :tag "9607"}
            {:longitude -79.42874, :title "Queen St West At Dufferin St", :latitude 43.6421499, :tag "9255"}
            {:longitude -79.4265499, :title "Queen St West At Gladstone Ave East Side", :latitude 43.64258, :tag "10205"}
            {:longitude -79.42449, :title "Queen St West At Abell St", :latitude 43.6429999, :tag "5899"}
            {:longitude -79.4225299, :title "Queen St West At Dovercourt Rd", :latitude 43.64339, :tag "8263"}
            {:longitude -79.41907, :title "Queen St West At Ossington Ave", :latitude 43.6440799, :tag "9093"}
            {:longitude -79.4165499, :title "Queen St West At Shaw St", :latitude 43.64457, :tag "2140"}
            {:longitude -79.4133199, :title "Queen St West At Strachan Ave", :latitude 43.64525, :tag "5250"}
            {:longitude -79.4100399, :title "Queen St West At Niagara St", :latitude 43.64593, :tag "3896"}
            {:longitude -79.40675, :title "Queen St West At Tecumseth St", :latitude 43.6465999, :tag "3399"}
            {:longitude -79.4042, :title "Queen St West At Bathurst St", :latitude 43.6471199, :tag "4514"}
            {:longitude -79.3998699, :title "Queen St West At Augusta Ave", :latitude 43.64797, :tag "8269"}
            {:longitude -79.3967, :title "Queen St West At Spadina Ave", :latitude 43.6486199, :tag "10294"}
            {:longitude -79.39377, :title "Queen St West At Peter St", :latitude 43.6492399, :tag "7060"}
            {:longitude -79.3911699, :title "Queen St West At John St", :latitude 43.64984, :tag "6319"}
            {:longitude -79.38995, :title "Queen St West At McCaul St", :latitude 43.6500599, :tag "10244"}
            {:longitude -79.3869299, :title "Queen St West At University Ave (Osgoode Station)", :latitude 43.6507, :tag "7321"}
            {:longitude -79.3850499, :title "Queen St West At York St", :latitude 43.6511, :tag "9183"}
            {:longitude -79.3821799, :title "Queen St West At Bay St", :latitude 43.6517, :tag "3575"}
            {:longitude -79.37945, :title "Queen St West At Yonge St (Queen Station)", :latitude 43.6522899, :tag "6108"}
            {:longitude -79.37829, :title "Queen St East At Victoria St (St Michaels Hospital)", :latitude 43.6525899, :tag "2666"}
            {:longitude -79.3758699, :title "Queen St East At Church St", :latitude 43.65309, :tag "9691"}
            {:longitude -79.3733899, :title "Queen St East At Jarvis St", :latitude 43.65362, :tag "8082"}
            {:longitude -79.3695599, :title "Queen St East At Sherbourne St", :latitude 43.65444, :tag "4584"}
            {:longitude -79.3672699, :title "Queen St East At Ontario St", :latitude 43.65493, :tag "7480"}
            {:longitude -79.3645899, :title "Queen St East At Parliament St", :latitude 43.65551, :tag "1300"}
            {:longitude -79.3618699, :title "Queen St East At Sackville St", :latitude 43.65612, :tag "5087"}
            {:longitude -79.3590199, :title "Queen St East At Sumach St", :latitude 43.65675, :tag "10054"}
            {:longitude -79.3567, :title "Queen St East At River St", :latitude 43.6572499, :tag "1301"}
            {:longitude -79.35254, :title "Queen St East At Carroll St", :latitude 43.6582099, :tag "6678"}
            {:longitude -79.35013, :title "Queen St East At Broadview Ave", :latitude 43.6587499, :tag "3443"}
            {:longitude -79.3471099, :title "Queen St East At Saulter St East Side", :latitude 43.65943, :tag "7665"}
            {:longitude -79.34452, :title "Queen St East At Empire Ave", :latitude 43.6600099, :tag "7234"}
            {:longitude -79.34269, :title "Queen St East At Logan Ave", :latitude 43.6604099, :tag "3836"}
            {:longitude -79.34024, :title "Queen St East At Carlaw Ave", :latitude 43.6609599, :tag "2338"}
            {:longitude -79.33802, :title "Queen St East At Pape Ave", :latitude 43.6614899, :tag "1944"}
            {:longitude -79.33544, :title "Queen St East At Caroline Ave", :latitude 43.6620299, :tag "9938"}
            {:longitude -79.3329099, :title "Queen St East At Jones Ave", :latitude 43.6626, :tag "1739"}
            {:longitude -79.33042, :title "Queen St East At Leslie St", :latitude 43.6631599, :tag "4456"}
            {:longitude -79.3274199, :title "Queen St East At Laing St", :latitude 43.66384, :tag "3271"}
            {:longitude -79.32551, :title "Queen St East At Greenwood Ave", :latitude 43.6642699, :tag "3060"}
            {:longitude -79.3226, :title "Queen St East At Connaught Ave", :latitude 43.6649199, :tag "7820"}
            {:longitude -79.3198699, :title "Queen St East At Woodward Ave", :latitude 43.66554, :tag "6209"}
            {:longitude -79.3167999, :title "Queen St East At Coxwell Ave", :latitude 43.66623, :tag "779"}
            {:longitude -79.31283, :title "Queen St East At Kingston Rd", :latitude 43.6670699, :tag "489"}
            {:longitude -79.3093099, :title "Queen St East At Sarah Ashbridge Ave", :latitude 43.6679, :tag "1260"}
            {:longitude -79.306, :title "Queen St East At Woodbine Ave", :latitude 43.6686399, :tag "4342"}
            {:longitude -79.3041099, :title "Queen St East At Kippendavie Ave", :latitude 43.66908, :tag "4110"}
            {:longitude -79.30094, :title "Queen St East At Waverley Rd", :latitude 43.6697799, :tag "1660"}
            {:longitude -79.2980599, :title "Queen St East At Lee Ave", :latitude 43.67041, :tag "5019"}
            {:longitude -79.29595, :title "Queen St East At Wineva Ave", :latitude 43.6708699, :tag "4425"}
            {:longitude -79.2936499, :title "Queen St East At Scarboro Beach Blvd", :latitude 43.67134, :tag "3559"}
            {:longitude -79.2911699, :title "Queen St East At Maclean Ave", :latitude 43.67185, :tag "3808"}
            {:longitude -79.2876999, :title "Queen St East At Beech Ave", :latitude 43.67258, :tag "10371"}
            {:longitude -79.28535, :title "Queen St East At Silver Birch Ave", :latitude 43.6730499, :tag "4598"}
            {:longitude -79.2825399, :title "Queen St East At Neville Park Blvd", :latitude 43.67363, :tag "4364"}
            {:longitude -79.28172, :title "Queen St West At Neville Park Blvd", :latitude 43.6739099, :tag "4143_ar"}
            {:longitude -79.47871, :title "Humber Loop At The Queensway", :latitude 43.6310799, :tag "473"}
            {:longitude -79.4413099, :title "Queen St West At Beaty Ave", :latitude 43.63963, :tag "7830"}
            {:longitude -79.4331899, :title "Queen St West At Cowan Ave", :latitude 43.64126, :tag "7293"}
            {:longitude -79.4109999, :title "Queen St West At Walnut Ave East Side", :latitude 43.64576, :tag "9190"}
            {:longitude -79.40054, :title "Queen St West At Denison Ave", :latitude 43.6478099, :tag "2372"}
            {:longitude -79.3635499, :title "Queen St East At Power St", :latitude 43.65574, :tag "9673"}
            {:longitude -79.34369, :title "Queen St West At Booth Ave", :latitude 43.6601999, :tag "5760"}
            {:longitude -79.2922299, :title "Queen St East At Glen Manor Dr East Side", :latitude 43.67164, :tag "9260"}
            {:longitude -79.28172, :title "Queen St West At Neville Park Blvd", :latitude 43.6739099, :tag "4143"}
            {:longitude -79.28484, :title "Queen St East At Silver Birch Ave", :latitude 43.6732699, :tag "3196"}
            {:longitude -79.28865, :title "Queen St East At Sprucehill Rd", :latitude 43.6724799, :tag "3869"}
            {:longitude -79.2924299, :title "Queen St East At Glen Manor Dr", :latitude 43.67169, :tag "6992"}
            {:longitude -79.2953099, :title "Queen St East At Wineva Ave", :latitude 43.6711, :tag "10076"}
            {:longitude -79.2978899, :title "Queen St East At Lee Ave", :latitude 43.67056, :tag "3923"}
            {:longitude -79.3007899, :title "Queen St East At Waverley Rd", :latitude 43.66992, :tag "8813"}
            {:longitude -79.3034399, :title "Queen St East At Elmer Ave", :latitude 43.66933, :tag "8746"}
            {:longitude -79.3064699, :title "Queen St East At Woodbine Ave West Side", :latitude 43.66866, :tag "3307"}
            {:longitude -79.30893, :title "Queen St East At Lockwood Rd", :latitude 43.6681099, :tag "4371"}
            {:longitude -79.31214, :title "Queen St East At Kingston Rd", :latitude 43.6673999, :tag "9143"}
            {:longitude -79.31645, :title "Queen St East At Coxwell Ave", :latitude 43.6664299, :tag "4034"}
            {:longitude -79.31989, :title "Queen St East At Kent Rd", :latitude 43.6656399, :tag "1997"}
            {:longitude -79.32221, :title "Queen St East At Connaught Ave", :latitude 43.6651199, :tag "6156"}
            {:longitude -79.32518, :title "Queen St East At Greenwood Ave", :latitude 43.6644599, :tag "1189"}
            {:longitude -79.3277499, :title "Queen St East At Alton Ave", :latitude 43.66387, :tag "3385"}
            {:longitude -79.3301299, :title "Queen St East At Leslie St", :latitude 43.66333, :tag "8882"}
            {:longitude -79.3325599, :title "Queen St East At Jones Ave", :latitude 43.66279, :tag "746"}
            {:longitude -79.3357999, :title "Queen St East At Brooklyn Ave", :latitude 43.66207, :tag "1744"}
            {:longitude -79.3376199, :title "Queen St East At Pape Ave", :latitude 43.66166, :tag "2650"}
            {:longitude -79.33995, :title "Queen St East At Carlaw Ave", :latitude 43.6611499, :tag "3577"}
            {:longitude -79.34237, :title "Queen St East At Logan Ave", :latitude 43.6605999, :tag "6902"}
            {:longitude -79.34408, :title "Queen St East At Empire Ave", :latitude 43.6602199, :tag "6682"}
            {:longitude -79.34655, :title "Queen St East At Boulton Ave", :latitude 43.6596599, :tag "3384"}
            {:longitude -79.34956, :title "Queen St East At Broadview Ave", :latitude 43.6589899, :tag "7385"}
            {:longitude -79.3519899, :title "Queen St East At Carroll St", :latitude 43.65844, :tag "1070"}
            {:longitude -79.35641, :title "Queen St East At River St", :latitude 43.6574499, :tag "10272"}
            {:longitude -79.35882, :title "Queen St East At Sumach St", :latitude 43.6569099, :tag "8731"}
            {:longitude -79.36135, :title "Queen St East At Sackville St", :latitude 43.6563499, :tag "7217"}
            {:longitude -79.3642699, :title "Queen St East At Parliament St", :latitude 43.65568, :tag "5599"}
            {:longitude -79.3667999, :title "Queen St East At Ontario St", :latitude 43.65515, :tag "7950"}
            {:longitude -79.3693199, :title "Queen St East At Sherbourne St", :latitude 43.6546, :tag "5706"}
            {:longitude -79.3730699, :title "Queen St East At Jarvis St", :latitude 43.65382, :tag "9301"}
            {:longitude -79.37556, :title "Queen St East At Church St", :latitude 43.6532799, :tag "9043"}
            {:longitude -79.3778099, :title "Queen St East At Victoria St (St Michaels Hospital)", :latitude 43.65278, :tag "1958"}
            {:longitude -79.37909, :title "Queen St East At Yonge St (Queen Station)", :latitude 43.6525499, :tag "2332"}
            {:longitude -79.3812599, :title "Queen St West At Bay St", :latitude 43.65206, :tag "2240"}
            {:longitude -79.38623, :title "Queen St West At University Ave (Osgoode Station)", :latitude 43.6510199, :tag "2086"}
            {:longitude -79.3879199, :title "Queen St West At Simcoe St West Side", :latitude 43.65062, :tag "6139"}
            {:longitude -79.38927, :title "Queen St West At McCaul St", :latitude 43.6503099, :tag "7205"}
            {:longitude -79.3909799, :title "Queen St West At John St", :latitude 43.64996, :tag "6851"}
            {:longitude -79.3932799, :title "Queen St West At Soho St", :latitude 43.64955, :tag "704"}
            {:longitude -79.39619, :title "Queen St West At Spadina Ave", :latitude 43.6488599, :tag "1653"}
            {:longitude -79.3995299, :title "Queen St West At Augusta Ave", :latitude 43.64813, :tag "3624"}
            {:longitude -79.4036899, :title "Queen St West At Bathurst St", :latitude 43.64734, :tag "10102"}
            {:longitude -79.40644, :title "Queen St West At Plamerston Ave", :latitude 43.6467599, :tag "7899"}
            {:longitude -79.4094099, :title "Queen St West At Claremont St", :latitude 43.64615, :tag "8418"}
            {:longitude -79.4130499, :title "Queen St West At Strachan Ave", :latitude 43.6454, :tag "7639"}
            {:longitude -79.4161399, :title "Queen St West At Shaw St", :latitude 43.64478, :tag "8686"}
            {:longitude -79.41861, :title "Queen St West At Ossington Ave", :latitude 43.6442799, :tag "9071"}
            {:longitude -79.42209, :title "Queen St West At Dovercourt Rd", :latitude 43.6435699, :tag "9335"}
            {:longitude -79.4245799, :title "Queen St West At Beaconsfield Ave", :latitude 43.64306, :tag "6616"}
            {:longitude -79.42688, :title "Queen St West At Gladstone Ave", :latitude 43.6425999, :tag "6528"}
            {:longitude -79.4291599, :title "Queen St West At Dufferin St West Side", :latitude 43.64219, :tag "14701"}
            {:longitude -79.43064, :title "Queen St West At Gwynne Ave West Side", :latitude 43.6418599, :tag "5984"}
            {:longitude -79.43223, :title "Queen St West At Brock Ave", :latitude 43.6415299, :tag "7718"}
            {:longitude -79.4340399, :title "Queen St West At Ohara Ave", :latitude 43.64117, :tag "1111"}
            {:longitude -79.4364399, :title "Queen St West At Lansdowne Ave", :latitude 43.64067, :tag "5294"}
            {:longitude -79.44076, :title "Queen St West At Sorauren Ave", :latitude 43.6398099, :tag "3798"}
            {:longitude -79.4433999, :title "Queen St West At Triller Ave", :latitude 43.6393, :tag "4692"}
            {:longitude -79.44584, :title "Queen St West At Roncesvalles Ave", :latitude 43.6388399, :tag "9459"}
            {:longitude -79.45104, :title "The Queensway At Glendale Ave (St Josephs Hospital)", :latitude 43.6393199, :tag "6877"}
            {:longitude -79.45423, :title "The Queensway At Parkside Dr", :latitude 43.6397599, :tag "8938"}
            {:longitude -79.4605999, :title "The Queensway At Colborne Lodge Dr West Side", :latitude 43.6394, :tag "10473"}
            {:longitude -79.46697, :title "The Queensway At Ellis Ave West Side", :latitude 43.6376899, :tag "10444"}
            {:longitude -79.47071, :title "The Queensway At Windermere Ave West Side", :latitude 43.6370999, :tag "10441"}
            {:longitude -79.47366, :title "The Queensway At South Kingsway", :latitude 43.6354199, :tag "3374"}
            {:longitude -79.47861, :title "Humber Loop At The Queensway", :latitude 43.6311899, :tag "474"}
            {:longitude -79.47879, :title "Humber Loop At The Queensway", :latitude 43.6309499, :tag "472"}
            {:longitude -79.47836, :title "Opposite 2111 Lake Shore Blvd West", :latitude 43.6289599, :tag "1227"}
            {:longitude -79.4799099, :title "Opposite 2155 Lake Shore Blvd West West Side", :latitude 43.62554, :tag "9377"}
            {:longitude -79.4815399, :title "Lake Shore Blvd West At Park Lawn Rd West Side", :latitude 43.62262, :tag "6830"}
            {:longitude -79.4831699, :title "Lake Shore Blvd West At Legion Rd", :latitude 43.62061, :tag "5669"}
            {:longitude -79.48644, :title "Lake Shore Blvd West At Louisa St", :latitude 43.6190299, :tag "8858"}
            {:longitude -79.4873799, :title "Lake Shore Blvd West At Burlington St", :latitude 43.61744, :tag "5667"}
            {:longitude -79.48869, :title "Lake Shore Blvd West At Superior Ave", :latitude 43.6151099, :tag "2156"}
            {:longitude -79.4893799, :title "Lake Shore Blvd West At Mimico Ave", :latitude 43.6138599, :tag "9148"}
            {:longitude -79.4901299, :title "Lake Shore Blvd West At Hillside Ave", :latitude 43.61127, :tag "3706"}
            {:longitude -79.4904299, :title "Lake Shore Blvd West At Symons St", :latitude 43.6086299, :tag "6466"}
            {:longitude -79.4905899, :title "Lake Shore Blvd West At Lake Cres", :latitude 43.6070899, :tag "4085"}
            {:longitude -79.49303, :title "Lake Shore Blvd West At Royal York Rd", :latitude 43.6038199, :tag "5230"}
            {:longitude -79.49836, :title "Lake Shore Blvd West At First St", :latitude 43.6023799, :tag "10320"}
            {:longitude -79.5006399, :title "Lake Shore Blvd West At Third St", :latitude 43.60188, :tag "1353"}
            {:longitude -79.50294, :title "Lake Shore Blvd West At Fifth St", :latitude 43.6013699, :tag "4223"}
            {:longitude -79.5050899, :title "Lake Shore Blvd West At Islington Ave", :latitude 43.6009, :tag "6827"}
            {:longitude -79.50846, :title "Lake Shore Blvd West At Tenth St", :latitude 43.6001499, :tag "1258"}
            {:longitude -79.5117099, :title "Lake Shore Blvd West At Thirteenth St", :latitude 43.59944, :tag "9222"}
            {:longitude -79.51386, :title "Lake Shore Blvd West At Fifteenth St", :latitude 43.5989699, :tag "1926"}
            {:longitude -79.51675, :title "Lake Shore Blvd West At Kipling Ave", :latitude 43.5983299, :tag "9382"}
            {:longitude -79.5212499, :title "Lake Shore Blvd West At Twenty Second St", :latitude 43.59733, :tag "1593"}
            {:longitude -79.52427, :title "Lake Shore Blvd West At Twenty Sixth St", :latitude 43.5966699, :tag "9317"}
            {:longitude -79.5281799, :title "Lake Shore Blvd West At Twenty Ninth St", :latitude 43.5958, :tag "6463"}
            {:longitude -79.52966, :title "Lake Shore Blvd West At Thirtieth St", :latitude 43.5954599, :tag "1737"}
            {:longitude -79.53379, :title "Lake Shore Blvd West At Long Branch Ave", :latitude 43.5944499, :tag "5577"}
            {:longitude -79.53798, :title "Lake Shore Blvd West At Thirty Seventh St", :latitude 43.5934899, :tag "8445"}
            {:longitude -79.5411947, :title "Lake Shore Blvd West At Thirty Ninth St West Side", :latitude 43.5927685, :tag "3811"}
            {:longitude -79.54412, :title "Long Branch Loop", :latitude 43.5918099, :tag "1750_ar"}
            {:longitude -79.47871, :title "Humber Loop At The Queensway", :latitude 43.6310799, :tag "473_ar"}
            {:longitude -79.36318, :title "Queen St East At Trefann St", :latitude 43.6559399, :tag "6899"}
            {:longitude -79.4004799, :title "Queen St West At Denison Ave", :latitude 43.64798, :tag "6153"}
            {:longitude -79.4110299, :title "Queen St West At Bellwoods Ave West Side", :latitude 43.64581, :tag "8568"}
            {:longitude -79.44198, :title "Queen St West At Callender St", :latitude 43.6395699, :tag "10029"}])

(deftest test-make-db
  (testing ""
    (let [db (geodb/make-db 0.1)]
      (is (not (nil? db))))))

(deftest test-add-object
  (testing "Adding objects"
    (let [db (geodb/make-db 0.1)]
      (doseq [stop stops]
        (geodb/add-object db stop))
      (is (= (count stops) (geodb/db-size db))))))

(deftest test-find-objects
  (testing "Finding objects"
    (let [db (geodb/make-db 0.1)]
      ;; Populate the database
      (doseq [stop stops]
        (geodb/add-object db stop))
      ;; Find objects
      (let [objects (geodb/find-objects db {:latitude 43.60023 :longitude -79.50257} 0.15)]
        (prn objects)
        ;; Make sure we found 6 objects
        (is (= 2 (count objects)))
        ;; Make sure all objects have a :distance field
        (doseq [object objects]
          (is (contains? object :object))
          (is (contains? object :position))
          (is (contains? object :distance)))
        ;; Make sure we got the right ones back
        (is (= "7245" (-> (nth objects 0) :object :tag)))
        (is (= "4223" (-> (nth objects 1) :object :tag)))
        ;; Make sure the objects are sorted
        (is (apply < (map #(:distance %) objects)))))))
