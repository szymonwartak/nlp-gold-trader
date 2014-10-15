
library(stringr)
library(data.table)
basedir = "/Users/Szymon/Dropbox/dev/gold/data/"

# files = paste0(basedir,"nytimes/",Filter(function(x) { str_detect(x, "*world.tsv") }, list.files(basedir)))
       
# multmerge = function(mypath){
#   filenames=list.files(path=files, full.names=TRUE)
#   datalist = lapply(files, function(x){ fread(x, colClasses="character")})
#   data=Reduce(function(x,y) {rbind(x,y)}, datalist)
# }
# data = multmerge(files)

d1 = fread(paste0(basedir,"nytimes-sentiment.tsv"))
d2 = fread(paste0(basedir,"gold-prices"))

# setup price (goal) data
gold = data.frame(
  date = strptime(d2$Date, "%b %d, %Y"),
  prices = sapply(d2$"Change %", function(x) as.numeric(as.numeric(str_sub(x,0,str_length(x)-1))>0))
)

# 45.6% of direction same the following day
mean(prices[2:length(gold$prices)] == gold$prices[1:(length(gold$prices)-1)])

# setup sentiment features
#x=split(head(d1), data$date)[[1]]
bydate = lapply(split(d1, d1$date), function(x) {
  bydate = as.data.frame(x[,3:7,with=F] / apply(x[,3:7,with=F], 1, sum))
  row.names(bydate) = x$section
  unlist(bydate)
})
nytimes.sentiment = data.frame(date = unique(strptime(d1$date, "%Y%m%d")))
s = matrix(unlist(bydate), ncol=length(bydate[[1]]), byrow=T)
colnames(s) = names(bydate[[1]])
nytimes.sentiment = cbind(nytimes.sentiment, s)

# join the data sets
m1 = match(nytimes.sentiment$date, gold$date)
data = nytimes.sentiment[!is.na(m1),-1]
data$Class = as.factor(gold[m1[!is.na(m1)],"prices"])

# train model
library(caret)
inTraining = createDataPartition(data$Class, p = 0.75, list = FALSE)
training = data[inTraining, ]
testing = data[-inTraining, ]

fit = train(Class ~ ., data = training, 
            method = "gbm", 
            trControl = trainControl(method = "repeatedcv", number = 10, repeats = 10), 
            verbose = FALSE)
testPred = predict(fit, testing)
confusionMatrix(testPred, testing$Class)
