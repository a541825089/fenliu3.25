<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="工单类型" prop="ticketType">
        <el-select v-model="queryParams.ticketType" placeholder="请选择工单类型" clearable style="width: 240px">
          <el-option
            v-for="(dict, index) in dict.type.ticket_type"
            :key="dict.value + '_' + index"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="工单名称" prop="ticketName">
        <el-input
          v-model="queryParams.ticketName"
          placeholder="请输入工单名称"
          clearable
          style="width: 240px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="工单链接" prop="ticketLink">
        <el-input
          v-model="queryParams.ticketLink"
          placeholder="请输入工单链接"
          clearable
          style="width: 240px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable style="width: 240px">
          <el-option
            v-for="(dict, index) in dict.type.sys_normal_disable"
            :key="dict.value + '_' + index"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['system:ticket:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['system:ticket:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['system:ticket:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['system:ticket:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="ticketList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="工单ID" align="center" prop="ticketId" width="90" />
      <el-table-column label="工单类型" align="center" prop="ticketType" width="110">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.ticket_type" :value="scope.row.ticketType"/>
        </template>
      </el-table-column>
      <el-table-column label="工单名称" align="center" prop="ticketName" :show-overflow-tooltip="true" min-width="160" />
      <el-table-column label="工单链接" align="center" prop="ticketLink" :show-overflow-tooltip="true" min-width="180">
        <template slot-scope="scope">
          <el-link v-if="scope.row.ticketLink" type="primary" :href="scope.row.ticketLink" target="_blank">{{ scope.row.ticketLink }}</el-link>
        </template>
      </el-table-column>
      <el-table-column label="分流链接" align="center" prop="linkUrl" :show-overflow-tooltip="true" min-width="180">
        <template slot-scope="scope">
          <el-link v-if="scope.row.linkUrl" type="primary" :href="scope.row.linkUrl" target="_blank">{{ scope.row.linkUrl }}</el-link>
        </template>
      </el-table-column>
      <el-table-column label="号码类型" align="center" prop="numberType" width="120">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.ticket_number_type" :value="scope.row.numberType"/>
        </template>
      </el-table-column>
      <el-table-column label="开始时间" align="center" prop="startTime" width="160">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.startTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="到期时间" align="center" prop="endTime" width="160">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.endTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="工单总量" align="center" prop="totalCount" width="100" />
      <el-table-column label="完成数量" align="center" prop="finishCount" width="100" />
      <el-table-column label="完成率" align="center" width="120">
        <template slot-scope="scope">
          <span>{{ calcFinishRate(scope.row) }}%</span>
        </template>
      </el-table-column>
      <el-table-column label="剩余数量" align="center" width="100">
        <template slot-scope="scope">
          <span>{{ calcRemainCount(scope.row) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="剩余率" align="center" width="120">
        <template slot-scope="scope">
          <span>{{ calcRemainRate(scope.row) }}%</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status" width="90">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.sys_normal_disable" :value="scope.row.status"/>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="140">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)" v-hasPermi="['system:ticket:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)" v-hasPermi="['system:ticket:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <el-dialog :title="title" :visible.sync="open" width="900px" append-to-body>
      <el-form :key="formKey" ref="form" :model="form" :rules="rules" label-width="100px" autocomplete="off">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="工单类型" prop="ticketType">
              <el-select v-model="form.ticketType" placeholder="请选择工单类型" style="width: 100%">
                <el-option
                  v-for="(dict, index) in dict.type.ticket_type"
                  :key="dict.value + '_' + index"
                  :label="dict.label"
                  :value="dict.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="工单名称" prop="ticketName">
              <el-input v-model="form.ticketName" placeholder="请输入工单名称" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="工单链接" prop="ticketLink">
              <el-input v-model="form.ticketLink" placeholder="请输入工单链接" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="分流链接" prop="linkId">
              <el-select
                v-model="form.linkId"
                filterable
                remote
                reserve-keyword
                allow-create
                default-first-option
                placeholder="请选择/输入分流链接URL"
                :remote-method="remoteSearchLink"
                :loading="linkLoading"
                style="width: 100%"
              >
                <el-option
                  v-for="item in linkOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="号码类型" prop="numberType">
              <el-select v-model="form.numberType" placeholder="请选择号码类型" style="width: 100%">
                <el-option
                  v-for="(dict, index) in dict.type.ticket_number_type"
                  :key="dict.value + '_' + index"
                  :label="dict.label"
                  :value="dict.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="开始时间" prop="startTime">
              <el-date-picker v-model="form.startTime" type="datetime" value-format="yyyy-MM-dd HH:mm:ss" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="到期时间" prop="endTime">
              <el-date-picker v-model="form.endTime" type="datetime" value-format="yyyy-MM-dd HH:mm:ss" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="工单总量" prop="totalCount">
              <el-input-number v-model="form.totalCount" :min="0" :step="1" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="下号比率" prop="downRatio">
              <el-input-number v-model="form.downRatio" :min="0" :step="1" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="工单账号" prop="ticketAccount">
              <el-input v-model="form.ticketAccount" placeholder="请输入工单账号" autocomplete="new-password" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="工单密码" prop="ticketPassword">
              <el-input v-model="form.ticketPassword" placeholder="请输入工单密码" show-password autocomplete="new-password" />
            </el-form-item>
          </el-col>
        </el-row>

      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { addLink, listLink } from "@/api/system/link";
import { getDicts } from "@/api/system/dict/data";
import { addTicket, delTicket, getTicket, listTicket, updateTicket } from "@/api/system/ticket";

export default {
  name: "Ticket",
  dicts: [
    'sys_normal_disable',
    { type: 'ticket_type', request: (dictMeta) => getDicts(dictMeta.type).then(res => res.data) },
    { type: 'ticket_number_type', request: (dictMeta) => getDicts(dictMeta.type).then(res => res.data) }
  ],
  data() {
    return {
      loading: true,
      ids: [],
      single: true,
      multiple: true,
      showSearch: true,
      total: 0,
      ticketList: [],
      title: "",
      open: false,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        ticketType: null,
        ticketName: null,
        ticketLink: null,
        status: null
      },
      form: {},
      rules: {
        ticketType: [{ required: true, message: "工单类型不能为空", trigger: "change" }],
        ticketName: [{ required: true, message: "工单名称不能为空", trigger: "blur" }],
        ticketLink: [{ required: true, message: "工单链接不能为空", trigger: "blur" }],
        linkId: [{ required: true, message: "分流链接不能为空", trigger: "change" }],
        numberType: [{ required: true, message: "号码类型不能为空", trigger: "change" }],
        startTime: [{ required: true, message: "开始时间不能为空", trigger: "change" }],
        endTime: [{ required: true, message: "到期时间不能为空", trigger: "change" }],
        totalCount: [{ required: true, message: "工单总量不能为空", trigger: "change" }]
      },
      linkOptions: [],
      linkLoading: false,
      linkQuery: "",
      formKey: 0
    };
  },
  created() {
    this.getList();
    this.remoteSearchLink("");
  },
  methods: {
    getList() {
      this.loading = true;
      listTicket(this.queryParams)
        .then(response => {
          this.ticketList = response.rows;
          this.total = response.total;
        })
        .finally(() => {
          this.loading = false;
        });
    },
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.ticketId);
      this.single = selection.length !== 1;
      this.multiple = !selection.length;
    },
    reset() {
      const now = new Date();
      const end = new Date(now.getTime() + 24 * 60 * 60 * 1000);
      this.formKey += 1;
      this.form = {
        ticketId: null,
        ticketType: null,
        ticketName: null,
        ticketLink: null,
        linkId: null,
        numberType: null,
        startTime: this.parseTime(now),
        endTime: this.parseTime(end),
        totalCount: 0,
        finishCount: 0,
        downRatio: 4,
        ticketAccount: "",
        ticketPassword: "",
        status: "0"
      };
      this.resetForm("form");
    },
    cancel() {
      this.open = false;
      this.reset();
    },
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加工单管理";
    },
    handleUpdate(row) {
      this.reset();
      const ticketId = row.ticketId || this.ids;
      getTicket(ticketId).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改工单管理";
      });
    },
    normalizeLinkId(value) {
      if (value === null || typeof value === "undefined" || value === "") return null;
      if (typeof value === "number") return value;
      const s = String(value).trim();
      if (/^\d+$/.test(s)) return Number(s);
      return s;
    },
    ensureLinkId() {
      const v = this.normalizeLinkId(this.form.linkId);
      if (v === null) return Promise.resolve(null);
      if (typeof v === "number") {
        this.form.linkId = v;
        return Promise.resolve(v);
      }
      const url = v;
      return addLink({
        linkUrl: url,
        linkDescription: "",
        replyMsg: "",
        ipProtection: "1",
        isScramble: "1",
        status: "0",
        targetCountry: ""
      }).then(resp => {
        this.form.linkId = resp.data;
        return resp.data;
      });
    },
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (!valid) return;
        this.ensureLinkId().then(() => {
          if (this.form.ticketId != null) {
            updateTicket(this.form).then(() => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addTicket(this.form).then(() => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            });
          }
        });
      });
    },
    handleDelete(row) {
      const ticketIds = row.ticketId || this.ids;
      this.$modal.confirm('是否确认删除工单编号为"' + ticketIds + '"的数据项？').then(function() {
        return delTicket(ticketIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    handleExport() {
      this.download('system/ticket/export', {
        ...this.queryParams
      }, `ticket_${new Date().getTime()}.xlsx`)
    },
    calcFinishRate(row) {
      const total = Number(row.totalCount || 0);
      const finish = Number(row.finishCount || 0);
      if (total <= 0) return 0;
      return Math.min(100, Math.max(0, Math.round((finish / total) * 10000) / 100));
    },
    calcRemainCount(row) {
      const total = Number(row.totalCount || 0);
      const finish = Number(row.finishCount || 0);
      return Math.max(0, total - finish);
    },
    calcRemainRate(row) {
      const rate = this.calcFinishRate(row);
      return Math.round((100 - rate) * 100) / 100;
    },
    remoteSearchLink(query) {
      this.linkQuery = query;
      this.linkLoading = true;
      listLink({ pageNum: 1, pageSize: 1000, linkUrl: query, status: "0" }).then(resp => {
        const rows = resp.rows || [];
        this.linkOptions = rows.map(r => ({
          value: r.linkId,
          label: r.linkUrl
        }));
        this.linkLoading = false;
      }).catch(() => {
        this.linkLoading = false;
      });
    }
  }
};
</script>
