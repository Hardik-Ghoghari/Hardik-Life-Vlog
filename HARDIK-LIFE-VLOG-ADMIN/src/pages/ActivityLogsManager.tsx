import React, { useState, useEffect } from 'react';
import { History, Shield, Clock, Terminal } from 'lucide-react';
import { FirestoreService } from '../services/firestoreService';
import { AdminActivityLog } from '../types';

export const ActivityLogsManager: React.FC = () => {
  const [logs, setLogs] = useState<AdminActivityLog[]>([]);

  useEffect(() => {
    FirestoreService.getActivityLogs().then(setLogs);
  }, []);

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-white tracking-tight">Security & Admin Audit Logs</h1>
        <p className="text-xs text-gray-400">
          Immutable audit trail of all administrative actions, data edits, and broadcasts
        </p>
      </div>

      <div className="p-6 rounded-3xl bg-[#161616] border border-[#262626] shadow-xl">
        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs">
            <thead>
              <tr className="border-b border-[#262626] text-gray-400 uppercase tracking-wider font-semibold">
                <th className="pb-3">Timestamp</th>
                <th className="pb-3">Admin</th>
                <th className="pb-3">Action</th>
                <th className="pb-3">Details</th>
                <th className="pb-3">IP Address</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-[#222222]">
              {logs.map(log => (
                <tr key={log.id} className="hover:bg-[#1C1C1C] transition">
                  <td className="py-3 text-gray-400 font-mono">
                    {new Date(log.timestamp).toLocaleString()}
                  </td>
                  <td className="py-3 font-semibold text-white">
                    {log.adminEmail}
                  </td>
                  <td className="py-3">
                    <span className="px-2 py-0.5 rounded-full bg-amber-500/10 text-amber-400 font-mono text-[10px] font-bold">
                      {log.action}
                    </span>
                  </td>
                  <td className="py-3 text-gray-300 max-w-md truncate">
                    {log.details}
                  </td>
                  <td className="py-3 text-gray-500 font-mono">
                    {log.ipAddress || '127.0.0.1'}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};
