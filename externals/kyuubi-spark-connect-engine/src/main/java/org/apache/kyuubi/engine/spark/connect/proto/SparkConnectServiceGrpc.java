package org.apache.kyuubi.engine.spark.connect.proto;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 * <pre>
 * Main interface for the SparkConnect service.
 * </pre>
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.60.1)",
    comments = "Source: spark/connect/base.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class SparkConnectServiceGrpc {

  private SparkConnectServiceGrpc() {}

  public static final String SERVICE_NAME = "spark.connect.SparkConnectService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<ExecutePlanRequest,
      ExecutePlanResponse> getExecutePlanMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "ExecutePlan",
      requestType = ExecutePlanRequest.class,
      responseType = ExecutePlanResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
  public static io.grpc.MethodDescriptor<ExecutePlanRequest,
      ExecutePlanResponse> getExecutePlanMethod() {
    io.grpc.MethodDescriptor<ExecutePlanRequest, ExecutePlanResponse> getExecutePlanMethod;
    if ((getExecutePlanMethod = SparkConnectServiceGrpc.getExecutePlanMethod) == null) {
      synchronized (SparkConnectServiceGrpc.class) {
        if ((getExecutePlanMethod = SparkConnectServiceGrpc.getExecutePlanMethod) == null) {
          SparkConnectServiceGrpc.getExecutePlanMethod = getExecutePlanMethod =
              io.grpc.MethodDescriptor.<ExecutePlanRequest, ExecutePlanResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "ExecutePlan"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  ExecutePlanRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  ExecutePlanResponse.getDefaultInstance()))
              .setSchemaDescriptor(new SparkConnectServiceMethodDescriptorSupplier("ExecutePlan"))
              .build();
        }
      }
    }
    return getExecutePlanMethod;
  }

  private static volatile io.grpc.MethodDescriptor<AnalyzePlanRequest,
      AnalyzePlanResponse> getAnalyzePlanMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "AnalyzePlan",
      requestType = AnalyzePlanRequest.class,
      responseType = AnalyzePlanResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<AnalyzePlanRequest,
      AnalyzePlanResponse> getAnalyzePlanMethod() {
    io.grpc.MethodDescriptor<AnalyzePlanRequest, AnalyzePlanResponse> getAnalyzePlanMethod;
    if ((getAnalyzePlanMethod = SparkConnectServiceGrpc.getAnalyzePlanMethod) == null) {
      synchronized (SparkConnectServiceGrpc.class) {
        if ((getAnalyzePlanMethod = SparkConnectServiceGrpc.getAnalyzePlanMethod) == null) {
          SparkConnectServiceGrpc.getAnalyzePlanMethod = getAnalyzePlanMethod =
              io.grpc.MethodDescriptor.<AnalyzePlanRequest, AnalyzePlanResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "AnalyzePlan"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  AnalyzePlanRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  AnalyzePlanResponse.getDefaultInstance()))
              .setSchemaDescriptor(new SparkConnectServiceMethodDescriptorSupplier("AnalyzePlan"))
              .build();
        }
      }
    }
    return getAnalyzePlanMethod;
  }

  private static volatile io.grpc.MethodDescriptor<ConfigRequest,
      ConfigResponse> getConfigMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "Config",
      requestType = ConfigRequest.class,
      responseType = ConfigResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<ConfigRequest,
      ConfigResponse> getConfigMethod() {
    io.grpc.MethodDescriptor<ConfigRequest, ConfigResponse> getConfigMethod;
    if ((getConfigMethod = SparkConnectServiceGrpc.getConfigMethod) == null) {
      synchronized (SparkConnectServiceGrpc.class) {
        if ((getConfigMethod = SparkConnectServiceGrpc.getConfigMethod) == null) {
          SparkConnectServiceGrpc.getConfigMethod = getConfigMethod =
              io.grpc.MethodDescriptor.<ConfigRequest, ConfigResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "Config"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  ConfigRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  ConfigResponse.getDefaultInstance()))
              .setSchemaDescriptor(new SparkConnectServiceMethodDescriptorSupplier("Config"))
              .build();
        }
      }
    }
    return getConfigMethod;
  }

  private static volatile io.grpc.MethodDescriptor<AddArtifactsRequest,
      AddArtifactsResponse> getAddArtifactsMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "AddArtifacts",
      requestType = AddArtifactsRequest.class,
      responseType = AddArtifactsResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.CLIENT_STREAMING)
  public static io.grpc.MethodDescriptor<AddArtifactsRequest,
      AddArtifactsResponse> getAddArtifactsMethod() {
    io.grpc.MethodDescriptor<AddArtifactsRequest, AddArtifactsResponse> getAddArtifactsMethod;
    if ((getAddArtifactsMethod = SparkConnectServiceGrpc.getAddArtifactsMethod) == null) {
      synchronized (SparkConnectServiceGrpc.class) {
        if ((getAddArtifactsMethod = SparkConnectServiceGrpc.getAddArtifactsMethod) == null) {
          SparkConnectServiceGrpc.getAddArtifactsMethod = getAddArtifactsMethod =
              io.grpc.MethodDescriptor.<AddArtifactsRequest, AddArtifactsResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.CLIENT_STREAMING)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "AddArtifacts"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  AddArtifactsRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  AddArtifactsResponse.getDefaultInstance()))
              .setSchemaDescriptor(new SparkConnectServiceMethodDescriptorSupplier("AddArtifacts"))
              .build();
        }
      }
    }
    return getAddArtifactsMethod;
  }

  private static volatile io.grpc.MethodDescriptor<ArtifactStatusesRequest,
      ArtifactStatusesResponse> getArtifactStatusMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "ArtifactStatus",
      requestType = ArtifactStatusesRequest.class,
      responseType = ArtifactStatusesResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<ArtifactStatusesRequest,
      ArtifactStatusesResponse> getArtifactStatusMethod() {
    io.grpc.MethodDescriptor<ArtifactStatusesRequest, ArtifactStatusesResponse> getArtifactStatusMethod;
    if ((getArtifactStatusMethod = SparkConnectServiceGrpc.getArtifactStatusMethod) == null) {
      synchronized (SparkConnectServiceGrpc.class) {
        if ((getArtifactStatusMethod = SparkConnectServiceGrpc.getArtifactStatusMethod) == null) {
          SparkConnectServiceGrpc.getArtifactStatusMethod = getArtifactStatusMethod =
              io.grpc.MethodDescriptor.<ArtifactStatusesRequest, ArtifactStatusesResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "ArtifactStatus"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  ArtifactStatusesRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  ArtifactStatusesResponse.getDefaultInstance()))
              .setSchemaDescriptor(new SparkConnectServiceMethodDescriptorSupplier("ArtifactStatus"))
              .build();
        }
      }
    }
    return getArtifactStatusMethod;
  }

  private static volatile io.grpc.MethodDescriptor<InterruptRequest,
      InterruptResponse> getInterruptMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "Interrupt",
      requestType = InterruptRequest.class,
      responseType = InterruptResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<InterruptRequest,
      InterruptResponse> getInterruptMethod() {
    io.grpc.MethodDescriptor<InterruptRequest, InterruptResponse> getInterruptMethod;
    if ((getInterruptMethod = SparkConnectServiceGrpc.getInterruptMethod) == null) {
      synchronized (SparkConnectServiceGrpc.class) {
        if ((getInterruptMethod = SparkConnectServiceGrpc.getInterruptMethod) == null) {
          SparkConnectServiceGrpc.getInterruptMethod = getInterruptMethod =
              io.grpc.MethodDescriptor.<InterruptRequest, InterruptResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "Interrupt"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  InterruptRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  InterruptResponse.getDefaultInstance()))
              .setSchemaDescriptor(new SparkConnectServiceMethodDescriptorSupplier("Interrupt"))
              .build();
        }
      }
    }
    return getInterruptMethod;
  }

  private static volatile io.grpc.MethodDescriptor<ReattachExecuteRequest,
      ExecutePlanResponse> getReattachExecuteMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "ReattachExecute",
      requestType = ReattachExecuteRequest.class,
      responseType = ExecutePlanResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
  public static io.grpc.MethodDescriptor<ReattachExecuteRequest,
      ExecutePlanResponse> getReattachExecuteMethod() {
    io.grpc.MethodDescriptor<ReattachExecuteRequest, ExecutePlanResponse> getReattachExecuteMethod;
    if ((getReattachExecuteMethod = SparkConnectServiceGrpc.getReattachExecuteMethod) == null) {
      synchronized (SparkConnectServiceGrpc.class) {
        if ((getReattachExecuteMethod = SparkConnectServiceGrpc.getReattachExecuteMethod) == null) {
          SparkConnectServiceGrpc.getReattachExecuteMethod = getReattachExecuteMethod =
              io.grpc.MethodDescriptor.<ReattachExecuteRequest, ExecutePlanResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "ReattachExecute"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  ReattachExecuteRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  ExecutePlanResponse.getDefaultInstance()))
              .setSchemaDescriptor(new SparkConnectServiceMethodDescriptorSupplier("ReattachExecute"))
              .build();
        }
      }
    }
    return getReattachExecuteMethod;
  }

  private static volatile io.grpc.MethodDescriptor<ReleaseExecuteRequest,
      ReleaseExecuteResponse> getReleaseExecuteMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "ReleaseExecute",
      requestType = ReleaseExecuteRequest.class,
      responseType = ReleaseExecuteResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<ReleaseExecuteRequest,
      ReleaseExecuteResponse> getReleaseExecuteMethod() {
    io.grpc.MethodDescriptor<ReleaseExecuteRequest, ReleaseExecuteResponse> getReleaseExecuteMethod;
    if ((getReleaseExecuteMethod = SparkConnectServiceGrpc.getReleaseExecuteMethod) == null) {
      synchronized (SparkConnectServiceGrpc.class) {
        if ((getReleaseExecuteMethod = SparkConnectServiceGrpc.getReleaseExecuteMethod) == null) {
          SparkConnectServiceGrpc.getReleaseExecuteMethod = getReleaseExecuteMethod =
              io.grpc.MethodDescriptor.<ReleaseExecuteRequest, ReleaseExecuteResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "ReleaseExecute"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  ReleaseExecuteRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  ReleaseExecuteResponse.getDefaultInstance()))
              .setSchemaDescriptor(new SparkConnectServiceMethodDescriptorSupplier("ReleaseExecute"))
              .build();
        }
      }
    }
    return getReleaseExecuteMethod;
  }

  private static volatile io.grpc.MethodDescriptor<FetchErrorDetailsRequest,
      FetchErrorDetailsResponse> getFetchErrorDetailsMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "FetchErrorDetails",
      requestType = FetchErrorDetailsRequest.class,
      responseType = FetchErrorDetailsResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<FetchErrorDetailsRequest,
      FetchErrorDetailsResponse> getFetchErrorDetailsMethod() {
    io.grpc.MethodDescriptor<FetchErrorDetailsRequest, FetchErrorDetailsResponse> getFetchErrorDetailsMethod;
    if ((getFetchErrorDetailsMethod = SparkConnectServiceGrpc.getFetchErrorDetailsMethod) == null) {
      synchronized (SparkConnectServiceGrpc.class) {
        if ((getFetchErrorDetailsMethod = SparkConnectServiceGrpc.getFetchErrorDetailsMethod) == null) {
          SparkConnectServiceGrpc.getFetchErrorDetailsMethod = getFetchErrorDetailsMethod =
              io.grpc.MethodDescriptor.<FetchErrorDetailsRequest, FetchErrorDetailsResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "FetchErrorDetails"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  FetchErrorDetailsRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  FetchErrorDetailsResponse.getDefaultInstance()))
              .setSchemaDescriptor(new SparkConnectServiceMethodDescriptorSupplier("FetchErrorDetails"))
              .build();
        }
      }
    }
    return getFetchErrorDetailsMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static SparkConnectServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<SparkConnectServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<SparkConnectServiceStub>() {
        @Override
        public SparkConnectServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new SparkConnectServiceStub(channel, callOptions);
        }
      };
    return SparkConnectServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static SparkConnectServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<SparkConnectServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<SparkConnectServiceBlockingStub>() {
        @Override
        public SparkConnectServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new SparkConnectServiceBlockingStub(channel, callOptions);
        }
      };
    return SparkConnectServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static SparkConnectServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<SparkConnectServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<SparkConnectServiceFutureStub>() {
        @Override
        public SparkConnectServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new SparkConnectServiceFutureStub(channel, callOptions);
        }
      };
    return SparkConnectServiceFutureStub.newStub(factory, channel);
  }

  /**
   * <pre>
   * Main interface for the SparkConnect service.
   * </pre>
   */
  public interface AsyncService {

    /**
     * <pre>
     * Executes a request that contains the query and returns a stream of [[Response]].
     * It is guaranteed that there is at least one ARROW batch returned even if the result set is empty.
     * </pre>
     */
    default void executePlan(ExecutePlanRequest request,
                             io.grpc.stub.StreamObserver<ExecutePlanResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getExecutePlanMethod(), responseObserver);
    }

    /**
     * <pre>
     * Analyzes a query and returns a [[AnalyzeResponse]] containing metadata about the query.
     * </pre>
     */
    default void analyzePlan(AnalyzePlanRequest request,
                             io.grpc.stub.StreamObserver<AnalyzePlanResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getAnalyzePlanMethod(), responseObserver);
    }

    /**
     * <pre>
     * Update or fetch the configurations and returns a [[ConfigResponse]] containing the result.
     * </pre>
     */
    default void config(ConfigRequest request,
                        io.grpc.stub.StreamObserver<ConfigResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getConfigMethod(), responseObserver);
    }

    /**
     * <pre>
     * Add artifacts to the session and returns a [[AddArtifactsResponse]] containing metadata about
     * the added artifacts.
     * </pre>
     */
    default io.grpc.stub.StreamObserver<AddArtifactsRequest> addArtifacts(
        io.grpc.stub.StreamObserver<AddArtifactsResponse> responseObserver) {
      return io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall(getAddArtifactsMethod(), responseObserver);
    }

    /**
     * <pre>
     * Check statuses of artifacts in the session and returns them in a [[ArtifactStatusesResponse]]
     * </pre>
     */
    default void artifactStatus(ArtifactStatusesRequest request,
                                io.grpc.stub.StreamObserver<ArtifactStatusesResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getArtifactStatusMethod(), responseObserver);
    }

    /**
     * <pre>
     * Interrupts running executions
     * </pre>
     */
    default void interrupt(InterruptRequest request,
                           io.grpc.stub.StreamObserver<InterruptResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getInterruptMethod(), responseObserver);
    }

    /**
     * <pre>
     * Reattach to an existing reattachable execution.
     * The ExecutePlan must have been started with ReattachOptions.reattachable=true.
     * If the ExecutePlanResponse stream ends without a ResultComplete message, there is more to
     * continue. If there is a ResultComplete, the client should use ReleaseExecute with
     * </pre>
     */
    default void reattachExecute(ReattachExecuteRequest request,
                                 io.grpc.stub.StreamObserver<ExecutePlanResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getReattachExecuteMethod(), responseObserver);
    }

    /**
     * <pre>
     * Release an reattachable execution, or parts thereof.
     * The ExecutePlan must have been started with ReattachOptions.reattachable=true.
     * Non reattachable executions are released automatically and immediately after the ExecutePlan
     * RPC and ReleaseExecute may not be used.
     * </pre>
     */
    default void releaseExecute(ReleaseExecuteRequest request,
                                io.grpc.stub.StreamObserver<ReleaseExecuteResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getReleaseExecuteMethod(), responseObserver);
    }

    /**
     * <pre>
     * FetchErrorDetails retrieves the matched exception with details based on a provided error id.
     * </pre>
     */
    default void fetchErrorDetails(FetchErrorDetailsRequest request,
                                   io.grpc.stub.StreamObserver<FetchErrorDetailsResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getFetchErrorDetailsMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service SparkConnectService.
   * <pre>
   * Main interface for the SparkConnect service.
   * </pre>
   */
  public static abstract class SparkConnectServiceImplBase
      implements io.grpc.BindableService, AsyncService {

    @Override public final io.grpc.ServerServiceDefinition bindService() {
      return SparkConnectServiceGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service SparkConnectService.
   * <pre>
   * Main interface for the SparkConnect service.
   * </pre>
   */
  public static final class SparkConnectServiceStub
      extends io.grpc.stub.AbstractAsyncStub<SparkConnectServiceStub> {
    private SparkConnectServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected SparkConnectServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new SparkConnectServiceStub(channel, callOptions);
    }

    /**
     * <pre>
     * Executes a request that contains the query and returns a stream of [[Response]].
     * It is guaranteed that there is at least one ARROW batch returned even if the result set is empty.
     * </pre>
     */
    public void executePlan(ExecutePlanRequest request,
                            io.grpc.stub.StreamObserver<ExecutePlanResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncServerStreamingCall(
          getChannel().newCall(getExecutePlanMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Analyzes a query and returns a [[AnalyzeResponse]] containing metadata about the query.
     * </pre>
     */
    public void analyzePlan(AnalyzePlanRequest request,
                            io.grpc.stub.StreamObserver<AnalyzePlanResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getAnalyzePlanMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Update or fetch the configurations and returns a [[ConfigResponse]] containing the result.
     * </pre>
     */
    public void config(ConfigRequest request,
                       io.grpc.stub.StreamObserver<ConfigResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getConfigMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Add artifacts to the session and returns a [[AddArtifactsResponse]] containing metadata about
     * the added artifacts.
     * </pre>
     */
    public io.grpc.stub.StreamObserver<AddArtifactsRequest> addArtifacts(
        io.grpc.stub.StreamObserver<AddArtifactsResponse> responseObserver) {
      return io.grpc.stub.ClientCalls.asyncClientStreamingCall(
          getChannel().newCall(getAddArtifactsMethod(), getCallOptions()), responseObserver);
    }

    /**
     * <pre>
     * Check statuses of artifacts in the session and returns them in a [[ArtifactStatusesResponse]]
     * </pre>
     */
    public void artifactStatus(ArtifactStatusesRequest request,
                               io.grpc.stub.StreamObserver<ArtifactStatusesResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getArtifactStatusMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Interrupts running executions
     * </pre>
     */
    public void interrupt(InterruptRequest request,
                          io.grpc.stub.StreamObserver<InterruptResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getInterruptMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Reattach to an existing reattachable execution.
     * The ExecutePlan must have been started with ReattachOptions.reattachable=true.
     * If the ExecutePlanResponse stream ends without a ResultComplete message, there is more to
     * continue. If there is a ResultComplete, the client should use ReleaseExecute with
     * </pre>
     */
    public void reattachExecute(ReattachExecuteRequest request,
                                io.grpc.stub.StreamObserver<ExecutePlanResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncServerStreamingCall(
          getChannel().newCall(getReattachExecuteMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Release an reattachable execution, or parts thereof.
     * The ExecutePlan must have been started with ReattachOptions.reattachable=true.
     * Non reattachable executions are released automatically and immediately after the ExecutePlan
     * RPC and ReleaseExecute may not be used.
     * </pre>
     */
    public void releaseExecute(ReleaseExecuteRequest request,
                               io.grpc.stub.StreamObserver<ReleaseExecuteResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getReleaseExecuteMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * FetchErrorDetails retrieves the matched exception with details based on a provided error id.
     * </pre>
     */
    public void fetchErrorDetails(FetchErrorDetailsRequest request,
                                  io.grpc.stub.StreamObserver<FetchErrorDetailsResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getFetchErrorDetailsMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service SparkConnectService.
   * <pre>
   * Main interface for the SparkConnect service.
   * </pre>
   */
  public static final class SparkConnectServiceBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<SparkConnectServiceBlockingStub> {
    private SparkConnectServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected SparkConnectServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new SparkConnectServiceBlockingStub(channel, callOptions);
    }

    /**
     * <pre>
     * Executes a request that contains the query and returns a stream of [[Response]].
     * It is guaranteed that there is at least one ARROW batch returned even if the result set is empty.
     * </pre>
     */
    public java.util.Iterator<ExecutePlanResponse> executePlan(
        ExecutePlanRequest request) {
      return io.grpc.stub.ClientCalls.blockingServerStreamingCall(
          getChannel(), getExecutePlanMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Analyzes a query and returns a [[AnalyzeResponse]] containing metadata about the query.
     * </pre>
     */
    public AnalyzePlanResponse analyzePlan(AnalyzePlanRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getAnalyzePlanMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Update or fetch the configurations and returns a [[ConfigResponse]] containing the result.
     * </pre>
     */
    public ConfigResponse config(ConfigRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getConfigMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Check statuses of artifacts in the session and returns them in a [[ArtifactStatusesResponse]]
     * </pre>
     */
    public ArtifactStatusesResponse artifactStatus(ArtifactStatusesRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getArtifactStatusMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Interrupts running executions
     * </pre>
     */
    public InterruptResponse interrupt(InterruptRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getInterruptMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Reattach to an existing reattachable execution.
     * The ExecutePlan must have been started with ReattachOptions.reattachable=true.
     * If the ExecutePlanResponse stream ends without a ResultComplete message, there is more to
     * continue. If there is a ResultComplete, the client should use ReleaseExecute with
     * </pre>
     */
    public java.util.Iterator<ExecutePlanResponse> reattachExecute(
        ReattachExecuteRequest request) {
      return io.grpc.stub.ClientCalls.blockingServerStreamingCall(
          getChannel(), getReattachExecuteMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Release an reattachable execution, or parts thereof.
     * The ExecutePlan must have been started with ReattachOptions.reattachable=true.
     * Non reattachable executions are released automatically and immediately after the ExecutePlan
     * RPC and ReleaseExecute may not be used.
     * </pre>
     */
    public ReleaseExecuteResponse releaseExecute(ReleaseExecuteRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getReleaseExecuteMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * FetchErrorDetails retrieves the matched exception with details based on a provided error id.
     * </pre>
     */
    public FetchErrorDetailsResponse fetchErrorDetails(FetchErrorDetailsRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getFetchErrorDetailsMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service SparkConnectService.
   * <pre>
   * Main interface for the SparkConnect service.
   * </pre>
   */
  public static final class SparkConnectServiceFutureStub
      extends io.grpc.stub.AbstractFutureStub<SparkConnectServiceFutureStub> {
    private SparkConnectServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected SparkConnectServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new SparkConnectServiceFutureStub(channel, callOptions);
    }

    /**
     * <pre>
     * Analyzes a query and returns a [[AnalyzeResponse]] containing metadata about the query.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<AnalyzePlanResponse> analyzePlan(
        AnalyzePlanRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getAnalyzePlanMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Update or fetch the configurations and returns a [[ConfigResponse]] containing the result.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<ConfigResponse> config(
        ConfigRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getConfigMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Check statuses of artifacts in the session and returns them in a [[ArtifactStatusesResponse]]
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<ArtifactStatusesResponse> artifactStatus(
        ArtifactStatusesRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getArtifactStatusMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Interrupts running executions
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<InterruptResponse> interrupt(
        InterruptRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getInterruptMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Release an reattachable execution, or parts thereof.
     * The ExecutePlan must have been started with ReattachOptions.reattachable=true.
     * Non reattachable executions are released automatically and immediately after the ExecutePlan
     * RPC and ReleaseExecute may not be used.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<ReleaseExecuteResponse> releaseExecute(
        ReleaseExecuteRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getReleaseExecuteMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * FetchErrorDetails retrieves the matched exception with details based on a provided error id.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<FetchErrorDetailsResponse> fetchErrorDetails(
        FetchErrorDetailsRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getFetchErrorDetailsMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_EXECUTE_PLAN = 0;
  private static final int METHODID_ANALYZE_PLAN = 1;
  private static final int METHODID_CONFIG = 2;
  private static final int METHODID_ARTIFACT_STATUS = 3;
  private static final int METHODID_INTERRUPT = 4;
  private static final int METHODID_REATTACH_EXECUTE = 5;
  private static final int METHODID_RELEASE_EXECUTE = 6;
  private static final int METHODID_FETCH_ERROR_DETAILS = 7;
  private static final int METHODID_ADD_ARTIFACTS = 8;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final AsyncService serviceImpl;
    private final int methodId;

    MethodHandlers(AsyncService serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_EXECUTE_PLAN:
          serviceImpl.executePlan((ExecutePlanRequest) request,
              (io.grpc.stub.StreamObserver<ExecutePlanResponse>) responseObserver);
          break;
        case METHODID_ANALYZE_PLAN:
          serviceImpl.analyzePlan((AnalyzePlanRequest) request,
              (io.grpc.stub.StreamObserver<AnalyzePlanResponse>) responseObserver);
          break;
        case METHODID_CONFIG:
          serviceImpl.config((ConfigRequest) request,
              (io.grpc.stub.StreamObserver<ConfigResponse>) responseObserver);
          break;
        case METHODID_ARTIFACT_STATUS:
          serviceImpl.artifactStatus((ArtifactStatusesRequest) request,
              (io.grpc.stub.StreamObserver<ArtifactStatusesResponse>) responseObserver);
          break;
        case METHODID_INTERRUPT:
          serviceImpl.interrupt((InterruptRequest) request,
              (io.grpc.stub.StreamObserver<InterruptResponse>) responseObserver);
          break;
        case METHODID_REATTACH_EXECUTE:
          serviceImpl.reattachExecute((ReattachExecuteRequest) request,
              (io.grpc.stub.StreamObserver<ExecutePlanResponse>) responseObserver);
          break;
        case METHODID_RELEASE_EXECUTE:
          serviceImpl.releaseExecute((ReleaseExecuteRequest) request,
              (io.grpc.stub.StreamObserver<ReleaseExecuteResponse>) responseObserver);
          break;
        case METHODID_FETCH_ERROR_DETAILS:
          serviceImpl.fetchErrorDetails((FetchErrorDetailsRequest) request,
              (io.grpc.stub.StreamObserver<FetchErrorDetailsResponse>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @Override
    @SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_ADD_ARTIFACTS:
          return (io.grpc.stub.StreamObserver<Req>) serviceImpl.addArtifacts(
              (io.grpc.stub.StreamObserver<AddArtifactsResponse>) responseObserver);
        default:
          throw new AssertionError();
      }
    }
  }

  public static final io.grpc.ServerServiceDefinition bindService(AsyncService service) {
    return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
        .addMethod(
          getExecutePlanMethod(),
          io.grpc.stub.ServerCalls.asyncServerStreamingCall(
            new MethodHandlers<
              ExecutePlanRequest,
              ExecutePlanResponse>(
                service, METHODID_EXECUTE_PLAN)))
        .addMethod(
          getAnalyzePlanMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              AnalyzePlanRequest,
              AnalyzePlanResponse>(
                service, METHODID_ANALYZE_PLAN)))
        .addMethod(
          getConfigMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              ConfigRequest,
              ConfigResponse>(
                service, METHODID_CONFIG)))
        .addMethod(
          getAddArtifactsMethod(),
          io.grpc.stub.ServerCalls.asyncClientStreamingCall(
            new MethodHandlers<
              AddArtifactsRequest,
              AddArtifactsResponse>(
                service, METHODID_ADD_ARTIFACTS)))
        .addMethod(
          getArtifactStatusMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              ArtifactStatusesRequest,
              ArtifactStatusesResponse>(
                service, METHODID_ARTIFACT_STATUS)))
        .addMethod(
          getInterruptMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              InterruptRequest,
              InterruptResponse>(
                service, METHODID_INTERRUPT)))
        .addMethod(
          getReattachExecuteMethod(),
          io.grpc.stub.ServerCalls.asyncServerStreamingCall(
            new MethodHandlers<
              ReattachExecuteRequest,
              ExecutePlanResponse>(
                service, METHODID_REATTACH_EXECUTE)))
        .addMethod(
          getReleaseExecuteMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              ReleaseExecuteRequest,
              ReleaseExecuteResponse>(
                service, METHODID_RELEASE_EXECUTE)))
        .addMethod(
          getFetchErrorDetailsMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              FetchErrorDetailsRequest,
              FetchErrorDetailsResponse>(
                service, METHODID_FETCH_ERROR_DETAILS)))
        .build();
  }

  private static abstract class SparkConnectServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    SparkConnectServiceBaseDescriptorSupplier() {}

    @Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return Base.getDescriptor();
    }

    @Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("SparkConnectService");
    }
  }

  private static final class SparkConnectServiceFileDescriptorSupplier
      extends SparkConnectServiceBaseDescriptorSupplier {
    SparkConnectServiceFileDescriptorSupplier() {}
  }

  private static final class SparkConnectServiceMethodDescriptorSupplier
      extends SparkConnectServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    SparkConnectServiceMethodDescriptorSupplier(String methodName) {
      this.methodName = methodName;
    }

    @Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (SparkConnectServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new SparkConnectServiceFileDescriptorSupplier())
              .addMethod(getExecutePlanMethod())
              .addMethod(getAnalyzePlanMethod())
              .addMethod(getConfigMethod())
              .addMethod(getAddArtifactsMethod())
              .addMethod(getArtifactStatusMethod())
              .addMethod(getInterruptMethod())
              .addMethod(getReattachExecuteMethod())
              .addMethod(getReleaseExecuteMethod())
              .addMethod(getFetchErrorDetailsMethod())
              .build();
        }
      }
    }
    return result;
  }
}
