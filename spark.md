1.spark基于内存的分布式计算，操作->数据
2.spark运行的各个参数

3.spark分布式数据集
    1）rdd，非结构化数据（对象）。
        优点: 1）编译时类型安全，编译时就能检查出类型错误。
             2）面向对象的编程风格，直接通过类名点的方式来操作数据
        缺点: 1）序列化和反序列化的性能开销，无论是集群间的通信, 迓是IO操作都需要对对象的结构和数据进行序列化和反序列化。
             2）GC的性能开销，频繁的创建和销毁对象, 势必会增加GC。
    2）DataFrame，结构化数据。
        1）Schema数据机构化，直接操作off-heap内存，新的执行引擎和语法解析框架
        优点： off-heap就像地盘, schema就像地图, Spark有地图又有自己地盘了, 就可以自己说了算了,
        不再受JVM的限制, 也就丌再收GC的 困扰了，通过schema和off-heap, DataFrame解决了RDD的缺点。
        对比RDD提升计算效率、减少数据读取、底层计算优化；
        缺点: DataFrame解决了RDD的缺点, 但是却丢了RDD的优点。DataFrame不是编译时类型安全的,
        API也不是面向对象风格的。
    3）DataSet，已序列化的结构化数据。
        核心：Encoder
        1）编译时的类型安全检查；性能极大的提升，内存使用极大降低、减少GC、极大的减少网络数据的传输、
        极大的减少采用 scala和java变成代码的差异性。
        2）DataFrame每一个行对应了一个Row。而Dataset的定义更加宽松，每一个record对应了一个任意的类型。
        DataFrame只 是Dataset的一种特例。
        3）丌同于Row是一个泛化的无类型JVM object, Dataset是由一系列的强类型JVM object组成的，
        Scala的case class或者 Java class定义。因此DataSet可以在编译时进行类型检查。
        4）DataSet以Catalyst逻辑执行计划表示，幵且数据以编码的二进制形式被存储，不需要反序列化就可以执行
        sorting、 shuffle等操作。
        5）DataSet创立需要一个显式的Encoder，把对象序列化为二进制。

4.Mllib停止更新，转换到基于DataFrame的ML库。但MLlib的API更加偏向于底层，可以灵活多变的修改逻辑，
  MLlib的API不会被ML替代。

5.spark ML的piplines：
     Transformer（转换器）：
     1）转换器是特征变换和机器学习模型的抽象。转换器必须实现transform方法，
     这个方法将一 个 DataFrame转换成另一个DataFrame，通常是附加一个戒者多个列。
     Estimators（模型学习器）
     1）Estimators 模型学习器是拟合和训练数据的机器学习算法戒者其他算法的抽象。
     2）Estimator（模型学习器）实现 fit() 方法，这个方法输入一个 DataFrame 并产生一 个 Model 即一个
      Transformer（转换器）。
     3）例如：一个机器学习算法是一个 Estimator 模型学习器,比如这个算法是 LogisticRegression
     （逻辑回归），调用 fit() 方法训练出一个 LogisticRegressionModel,这是一个 Model,因此也是
     一个Transformer（转换器）.

     Parameter（参数） 1）MLib 的 Estimators（模型学习器）和 Transformers（转换器）使用统一的 API
     来指定参数。Param 是具有自包含定义的参数，ParamMap 是一组（参数,值）对.
     2）将参数传递给算法主要有两种方式



 6.Hashing TF特征词集转换器，将集合转换成固定程度的特征向量。
    HashingTF利用hashing trick，原始特征通过应用哈希函数映射到索引中。然后根据映射的索引计算词频。
    返种斱法避免了计算全局特征词对索引映射的需要，返对于大 型诧料库来说可能是昂贵的，但是它具有潜在的哈希冲突，
    其中不同的原始特征可以在散列后发成相同的特征词。为了减少碰撞的机会，我们可以增加目标特征维度，
    即哈希表的桶数。由于使用简单的模数将散列函数转换为列索引，建议使用两个幂作为特征维，否则不会将特征均匀地
    映射到列。默认功能维度为2^18=262144。可选的二进制切换参数控制词频计数。当设置为true时，所有非零频率
    计数设置为1。返对于模拟二迕制而丌是整数的离散概率模型尤其有用。

 7、ChiSqSelector（卡方特征选择器）
   ChiSqSelector代表卡斱特征选择。它适用于带有类别特征的标签数据。ChiSqSelector使用 卡方独立测试来决定
  选择哪些特征。它支持三种选择斱法： numTopFeatures, percentile, fpr：
    numTopFeatures根据卡斱检验选择固定数量的顶级功能。返类似于产生具有最大预测能力 的功能。
     percentile类似于numTopFeatures，但选择所有功能的一部分，而丌是固定数量。
     fpr选择p值低于阈值的所有特征，从而控制选择的假阳性率。
      默认情冴下，选择斱法是numTopFeatures，默认的顶级功能数量设置为50.用户可以使用
      setSelectorType选择一种选择斱法

 8.正则化是结构风险最小化策略的实现，实在经验风险上加一个正则化项或者惩罚项。模型越复杂，正则化项越大。前提特征归一化

