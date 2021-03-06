---
layout: global
title: Metrics System
group: Features
priority: 3
---

* Table of Contents
{:toc}

Metrics provide an insight into what is going on in the cluster. They are an invaluable resource for
monitoring and debugging. Tachyon has a configurable metrics system based on the [Coda Hale Metrics 
Library](https://github.com/dropwizard/metrics). In the metrics system, metrics sources are where 
the metrics generated, and metrics sinks consume the records generated by the metrics sources. 
The metrics system polls the metrics sources periodically and passes the metrics records to 
the metrics sinks.

Tachyon's metrics are partitioned into different instances corresponding to Tachyon components.
Within each instance, users can configure a set of sinks to which metrics are reported. The
following instances are currently supported:

* Master: The Tachyon standalone master process.
* Worker: The Tachyon standalone worker process.

Each instance can report to zero or more sinks.

* ConsoleSink: Outputs metrics values to the console.
* CsvSink: Exports metrics data to CSV files at regular intervals.
* JmxSink: Registers metrics for viewing in a JMX console.
* GraphiteSink: Sends metrics to a Graphite server.
* MetricsServlet: Adds a servlet in Web UI to serve metrics data as JSON data.

Some metrics like `BytesReadLocal` rely on data collected from client heartbeat. To get the accurate
metrics data, the client is expected to properly close the `TachyonFileSystem` client after using
it.

# Configuration
The metrics system is configured via a configuration file that Tachyon expects to be present at
`$TACHYON_HOME/conf/metrics.properties`. A custom file location can be specified via the
`tachyon.metrics.conf.file` configuration property. Tachyon provides a metrics.properties.template
under the conf directory which includes all configurable properties. By default, MetricsServlet
is enabled and you can send HTTP request "/metrics/json" to get a snapshot of all the registered
metrics in JSON format.

# Supported Metrics

Metrics are classified as:

* General: overall measures of the cluster (e.g. CapacityTotal).
* Logical Operations: number of operations performed (e.g. FilesCreated).
* RPC Invocations: number of RPC invocations per operation (e.g. CreateFileOps).

The following shows the details of the available metrics. 

## Master

### General

* CapacityTotal: Total capacity of the file system in bytes.
* CapacityUsed: Used capacity of the file system in bytes.
* CapacityFree: Free capacity of the file system in bytes.
* PathsTotal: Total number of files and directories in the file system.
* UnderFsCapacityTotal: Total capacity of the under file system in bytes.
* UnderFsCapacityUsed: Used capacity of the under file system in bytes.
* UnderFsCapacityFree: Free capacity of the under file system in bytes.
* Workers: Number of the workers.

### Logical Operations

* DirectoriesCreated: Total number of directories created.
* FileBlockInfosGot: Total number of the file block infos retrieved.
* FileInfosGot: Total number of the file infos retrieved.
* FilesCompleted: Total number of files completed.
* FilesCreated: Total number of files created.
* FilesFreed: Total number of files freed.
* FilesPersisted: Total number of the files persisted.
* FilesPinned: Total number of the files pinned.
* NewBlocksGot: Total number of new blocks got.
* PathsDeleted: Total number of files and directories deleted.
* PathsMounted: Total number of paths mounted.
* PathsRenamed: Total number of files and directories renamed.
* PathsUnmounted: Total number of paths unmounted.

### RPC Invocations

* CompleteFileOps: Total number of the CompleteFile operations.
* CreateDirectoryOps: Total number of the CreateDirectory operations.
* CreateFileOps: Total number of the CreateFile operations.
* DeletePathOps: Total number of the DeletePath operations.
* FreeFileOps: Total number of FreeFile operations.
* GetFileBlockInfoOps: Total number of GetFileBlockInfo operations.
* GetFileInfoOps: Total number of GetFileInfo operations.
* GetNewBlockOps: Total number of GetNewBlock operations.
* MountOps: Total number of Mount operations.
* RenamePathOps: Total number of the RenamePath operations.
* SetStateOps: Total number of the SetState operations.
* UnmountOps: Total number of Unmount operations.

## Worker

### General

* CapacityTotal: Total capacity of the worker in bytes.
* CapacityUsed: Used capacity of the worker in bytes.
* CapacityFree: Free capacity of the worker in bytes.

### Logical Operations

* BlocksAccessed: Total number of the blocks accessed.
* BlocksCached: Total number of blocks cached.
* BlocksCanceled: Total number of blocks canceled.
* BlocksDeleted: Total number of blocks deleted.
* BlocksEvicted: Total number of blocks evicted.
* BlocksPromoted: Total number of blocks promoted.
* BlocksReadLocal: Total number of blocks read locally from the worker.
* BlocksReadRemote: Total number of blocks read remotely from the worker.
* BlocksWrittenLocal: Total number of blocks written to the worker locally.
* BytesReadLocal: Total number of bytes read locally from the worker.
* BytesReadRemote: Total number of bytes read remotely from the worker.
* BytesReadUfs: Total number of bytes read from under file system on the worker.
* BytesWrittenLocal: Total number of bytes written to the worker locally.
* BytesWrittenUfs: Total number of bytes written to under file system on the worker.
