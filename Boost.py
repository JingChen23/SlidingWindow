import numpy as np
from sklearn import metrics
from sklearn.preprocessing import StandardScaler
scaler = StandardScaler()
from sklearn.ensemble import RandomForestClassifier
from sklearn import tree
from sklearn.linear_model import LogisticRegression
from sklearn.naive_bayes import BernoulliNB
from sklearn.neural_network import MLPClassifier
from sklearn.model_selection import cross_val_predict


# features: 0overLimit,1Amt,2suspiciousAmt,3ip,4times,5timeGapAvg,6timeGapVar,7AmtGapAvg,8AmtGap,9sign

train_set = np.loadtxt('D:/Jworkspace/4-5train_nordec.csv', delimiter=",")
train_Y = train_set[:,9]
train_X = np.concatenate((train_set[:,0:2], train_set[:,3:9]), axis= 1)
scaler.fit(train_X)

test_set = np.loadtxt('D:/Jworkspace/6test.csv', delimiter=",")
test_Y = test_set[:,9]
test_X = np.concatenate((test_set[:,0:2], test_set[:,3:9]), axis=1)
scaler.fit(test_X)
print ('Random Forest!')
clf = RandomForestClassifier(n_estimators=50)
clf.fit(train_X,train_Y)
prediction1 = clf.predict(test_X)

print (prediction1.__len__())
# print ('CART!')
# clf = tree.DecisionTreeClassifier()
# clf.fit(train_X,train_Y)
# prediction2 = clf.predict(test_X)

# print('Naive Bayes!')
# clf = BernoulliNB()
# clf.fit(train_X,train_Y)
# prediction3 = clf.predict(test_X)

print ('only rf:')
print (metrics.classification_report(test_Y, prediction1))
print (metrics.confusion_matrix(test_Y, prediction1))

train_set = np.loadtxt('D:/Jworkspace/4-5train.csv', delimiter=",")
train_Y = train_set[:,9]
train_X = np.concatenate((train_set[:,0:2], train_set[:,3:9]), axis= 1)
scaler.fit(train_X)

print('Neural Network!')
clf=MLPClassifier(hidden_layer_sizes=(20,30,))
clf.fit(train_X,train_Y)
prediction4 = clf.predict(test_X)

print ('only nn:')
print (metrics.classification_report(test_Y, prediction4))
print (metrics.confusion_matrix(test_Y, prediction4))


l = prediction1.__len__()
i = 0
print (l)

fobj = open('not_catch.csv','w')
f = open('D:/Jworkspace/6test.csv')

while (i < l):
    str = f.readline()
    if  prediction4[i] > 0:
        prediction1[i] = 1
    if  prediction1[i] < 1 and test_Y[i] > 0:
        fobj.write(str)
    i += 1
f.close()
fobj.close()
# answers_X = np.column_stack((prediction1,prediction2,prediction3,prediction4))
# answers_Y = test_Y

# clf=MLPClassifier(hidden_layer_sizes=(10,10,))
# print ('Boost training start!')
# prediction = cross_val_predict(clf,answers_X,answers_Y,cv = 3)



print ('predicting done, result:')
print (metrics.classification_report(test_Y, prediction1))
print (metrics.confusion_matrix(test_Y, prediction1))
