import React, { useState, useEffect } from 'react';
import { Bell, Send, CheckCircle, Radio, ExternalLink, Clock } from 'lucide-react';
import { FirestoreService } from '../services/firestoreService';
import { PushNotification, Vlog } from '../types';

interface NotificationsManagerProps {
  initialOpenModal?: boolean;
}

export const NotificationsManager: React.FC<NotificationsManagerProps> = () => {
  const [notifications, setNotifications] = useState<PushNotification[]>([]);
  const [vlogs, setVlogs] = useState<Vlog[]>([]);
  const [sending, setSending] = useState(false);
  const [successMsg, setSuccessMsg] = useState(false);

  // Form
  const [title, setTitle] = useState('New Vlog is Live! 🔥');
  const [message, setMessage] = useState('Check out the latest Hardik Life Vlog episode now streaming!');
  const [imageUrl, setImageUrl] = useState('');
  const [targetType, setTargetType] = useState<'all' | 'vlog' | 'short'>('vlog');
  const [targetVlogId, setTargetVlogId] = useState('');

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    const [n, v] = await Promise.all([
      FirestoreService.getNotifications(),
      FirestoreService.getVlogs()
    ]);
    setNotifications(n);
    setVlogs(v);
    if (v.length > 0) {
      setTargetVlogId(v[0].id);
    }
  };

  const handleSend = async (e: React.FormEvent) => {
    e.preventDefault();
    setSending(true);

    const targetId = targetType === 'vlog' ? targetVlogId : targetType === 'all' ? 'home' : 'shorts';

    const notif: PushNotification = {
      id: 'notif_' + Date.now(),
      title,
      message,
      imageUrl: imageUrl || undefined,
      targetType: targetType as any,
      targetId,
      sentAt: Date.now(),
      sentBy: 'Hardik'
    };

    await FirestoreService.sendNotification(notif);
    await loadData();
    setSending(false);
    setSuccessMsg(true);
    setTimeout(() => setSuccessMsg(false), 4000);
  };

  return (
    <div className="space-y-8 max-w-4xl">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-white tracking-tight">Push Notification Broadcast</h1>
          <p className="text-xs text-gray-400">
            Dispatch Firebase Cloud Messaging (FCM) notifications with deep links to viewer devices
          </p>
        </div>
        {successMsg && (
          <div className="px-3.5 py-1.5 rounded-xl bg-emerald-500/10 border border-emerald-500/30 text-emerald-400 text-xs font-bold flex items-center gap-2">
            <CheckCircle size={15} />
            FCM Broadcast Sent Successfully!
          </div>
        )}
      </div>

      {/* Broadcast Form */}
      <div className="p-6 lg:p-8 rounded-3xl bg-[#161616] border border-[#262626] shadow-xl">
        <form onSubmit={handleSend} className="space-y-5">
          <div>
            <label className="block text-xs font-semibold text-gray-300 uppercase tracking-wider mb-1.5">
              Notification Title *
            </label>
            <input
              type="text"
              required
              value={title}
              onChange={(e) => setTitle(e.target.value)}
              placeholder="e.g. New Vlog is Live! 🔥"
              className="w-full px-3.5 py-2.5 bg-[#1E1E1E] border border-[#303030] rounded-xl text-white text-sm focus:outline-none focus:border-amber-500 font-medium"
            />
          </div>

          <div>
            <label className="block text-xs font-semibold text-gray-300 uppercase tracking-wider mb-1.5">
              Message Body *
            </label>
            <textarea
              required
              rows={3}
              value={message}
              onChange={(e) => setMessage(e.target.value)}
              placeholder="Enter message visible on user lock screen..."
              className="w-full px-3.5 py-2.5 bg-[#1E1E1E] border border-[#303030] rounded-xl text-white text-sm focus:outline-none focus:border-amber-500"
            />
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div>
              <label className="block text-xs font-semibold text-gray-300 uppercase tracking-wider mb-1.5">
                Target Destination & Action
              </label>
              <select
                value={targetType}
                onChange={(e: any) => setTargetType(e.target.value)}
                className="w-full px-3.5 py-2.5 bg-[#1E1E1E] border border-[#303030] rounded-xl text-white text-sm focus:outline-none"
              >
                <option value="vlog">Open Specific Vlog (Deep link)</option>
                <option value="short">Open Shorts Feed</option>
                <option value="all">Open App Home Page</option>
              </select>
            </div>

            {targetType === 'vlog' && (
              <div>
                <label className="block text-xs font-semibold text-gray-300 uppercase tracking-wider mb-1.5">
                  Select Target Vlog
                </label>
                <select
                  value={targetVlogId}
                  onChange={(e) => setTargetVlogId(e.target.value)}
                  className="w-full px-3.5 py-2.5 bg-[#1E1E1E] border border-[#303030] rounded-xl text-white text-sm focus:outline-none"
                >
                  {vlogs.map(v => (
                    <option key={v.id} value={v.id}>{v.title}</option>
                  ))}
                </select>
              </div>
            )}
          </div>

          {/* Deep link preview badge */}
          <div className="p-3.5 rounded-xl bg-[#1C1C1C] border border-[#2B2B2B] text-xs font-mono text-amber-400 flex items-center gap-2">
            <Radio size={14} className="text-amber-500" />
            <span>Deep Link URI: hardiklifevlog://{targetType === 'vlog' ? `vlog/${targetVlogId}` : targetType}</span>
          </div>

          <div className="pt-2 flex justify-end">
            <button
              type="submit"
              disabled={sending}
              className="flex items-center gap-2 px-6 py-3 rounded-xl bg-gradient-to-r from-amber-500 to-orange-500 hover:from-amber-600 hover:to-orange-600 text-black font-extrabold text-sm shadow-xl shadow-amber-500/20 transition disabled:opacity-50"
            >
              <Send size={18} />
              <span>{sending ? 'Sending Broadcast...' : 'SEND NOTIFICATION TO ALL USERS'}</span>
            </button>
          </div>
        </form>
      </div>

      {/* Broadcast History */}
      <div className="p-6 rounded-3xl bg-[#161616] border border-[#262626] shadow-xl">
        <h3 className="text-base font-bold text-white mb-4 flex items-center gap-2">
          <Clock size={18} className="text-amber-400" />
          Broadcast Transmission Log
        </h3>
        <div className="space-y-3">
          {notifications.map(n => (
            <div key={n.id} className="p-4 rounded-2xl bg-[#1E1E1E] border border-[#2A2A2A] flex items-start justify-between gap-4">
              <div>
                <h4 className="text-sm font-bold text-white">{n.title}</h4>
                <p className="text-xs text-gray-300 mt-0.5">{n.message}</p>
                <div className="mt-2 flex items-center gap-3 text-[11px] text-gray-500 font-mono">
                  <span>Target: {n.targetType}</span>
                  <span>Sent by: {n.sentBy}</span>
                  <span>{new Date(n.sentAt).toLocaleString()}</span>
                </div>
              </div>
              <span className="px-2.5 py-1 rounded-full bg-emerald-500/10 text-emerald-400 text-[10px] font-bold uppercase shrink-0">
                Delivered
              </span>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};
